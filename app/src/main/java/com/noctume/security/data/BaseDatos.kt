package com.noctume.security.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.ColumnInfo
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true), Index(value = ["nombre"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val correo: String,
    val claveHash: String,
    val sal: String,
    val recordatorio: Boolean = false,
    val sesionActiva: Boolean = false,
    @ColumnInfo(defaultValue = "'Principiante'") val nivel: String = "Principiante",
    @ColumnInfo(defaultValue = "NULL") val fotoPath: String? = null
)

@Entity(tableName = "evaluaciones", indices = [Index("usuarioId")])
data class Evaluacion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val fecha: Long,
    val puntaje: Int,
    val respuestas: String,
    val hechas: String,
    @ColumnInfo(defaultValue = "NULL") val dispositivoId: Int? = null
)

@Entity(tableName = "dispositivos", indices = [Index("usuarioId")])
data class Dispositivo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val nombre: String,
    val sistema: String,
    val fecha: Long,
    @ColumnInfo(defaultValue = "0") val seleccionado: Boolean = false
)

@Entity(tableName = "solicitudes", indices = [Index("usuarioId")])
data class Solicitud(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val categoria: String,
    val descripcion: String,
    val estado: String = "Recibida",
    val fecha: Long
)

@Dao
interface NoctumeDao {
    @Query("SELECT * FROM usuarios WHERE sesionActiva = 1 LIMIT 1")
    fun usuarioActivo(): Flow<Usuario?>

    @Query("SELECT * FROM usuarios WHERE LOWER(correo) = LOWER(:dato) OR LOWER(nombre) = LOWER(:dato) LIMIT 1")
    suspend fun buscar(dato: String): Usuario?

    @Insert
    suspend fun insertarUsuario(usuario: Usuario): Long

    @Update
    suspend fun actualizarUsuario(usuario: Usuario)

    @Query("UPDATE usuarios SET sesionActiva = 0")
    suspend fun cerrarSesiones()

    @Query("UPDATE usuarios SET sesionActiva = 1 WHERE id = :id")
    suspend fun abrirSesion(id: Int)

    @Query("SELECT * FROM evaluaciones WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun evaluaciones(usuarioId: Int): Flow<List<Evaluacion>>

    @Insert
    suspend fun insertarEvaluacion(evaluacion: Evaluacion)

    @Update
    suspend fun actualizarEvaluacion(evaluacion: Evaluacion)

    @Query("SELECT * FROM dispositivos WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun dispositivos(usuarioId: Int): Flow<List<Dispositivo>>

    @Insert
    suspend fun insertarDispositivo(dispositivo: Dispositivo)

    @Update
    suspend fun actualizarDispositivo(dispositivo: Dispositivo)

    @Query("UPDATE dispositivos SET seleccionado = CASE WHEN id = :id THEN 1 ELSE 0 END WHERE usuarioId = :usuarioId")
    suspend fun establecerDispositivoActivo(usuarioId: Int, id: Int)

    @Query("DELETE FROM dispositivos WHERE id = :id")
    suspend fun eliminarDispositivo(id: Int)

    @Query("SELECT * FROM solicitudes WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun solicitudes(usuarioId: Int): Flow<List<Solicitud>>

    @Insert
    suspend fun insertarSolicitud(solicitud: Solicitud)

    @Update
    suspend fun actualizarSolicitud(solicitud: Solicitud)
}

@Database(entities = [Usuario::class, Evaluacion::class, Dispositivo::class, Solicitud::class], version = 3, exportSchema = false)
abstract class NoctumeDatabase : RoomDatabase() {
    abstract fun dao(): NoctumeDao

    companion object {
        @Volatile
        private var instancia: NoctumeDatabase? = null

        fun obtener(context: Context): NoctumeDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    NoctumeDatabase::class.java,
                    "noctune_datos.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build().also { instancia = it }
            }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE usuarios ADD COLUMN nivel TEXT NOT NULL DEFAULT 'Principiante'")
                db.execSQL("ALTER TABLE usuarios ADD COLUMN fotoPath TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE evaluaciones ADD COLUMN dispositivoId INTEGER DEFAULT NULL")
                db.execSQL("CREATE TABLE IF NOT EXISTS dispositivos (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, usuarioId INTEGER NOT NULL, nombre TEXT NOT NULL, sistema TEXT NOT NULL, fecha INTEGER NOT NULL, seleccionado INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_dispositivos_usuarioId ON dispositivos(usuarioId)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS solicitudes (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, usuarioId INTEGER NOT NULL, categoria TEXT NOT NULL, descripcion TEXT NOT NULL, estado TEXT NOT NULL, fecha INTEGER NOT NULL)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_solicitudes_usuarioId ON solicitudes(usuarioId)")
            }
        }
    }
}
