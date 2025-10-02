package apc.appcradle.radioappcradle.domain

data class Track(
    val id: Long,
    val name: String,
    val data: String,
    val duration: Int,
    val albumId: Long
)

