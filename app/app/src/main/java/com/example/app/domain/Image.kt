package com.example.app.domain

data class Image(
    val imageId: String,
    var urlOrRes: String,
    var metadata: String
) {
    /**
     * Sube una imagen local al servidor o almacenamiento en la nube.
     */
    fun uploadImage() {
        // @TODO Llamar al servicio de Firebase Storage o AWS S3
    }

    /**
     * Elimina la imagen del servidor y de la base de datos.
     */
    fun deleteImage() {
        // @TODO Borrar archivo en la nube y registro local
    }

    /**
     * Recupera o formatea la URL final para ser mostrada en la interfaz.
     */
    fun getImageUrl(): String {
        if (urlOrRes.startsWith("http")) {
            return urlOrRes
        }
        return "android.resource://com.example.app/drawable/$urlOrRes"
    }
}