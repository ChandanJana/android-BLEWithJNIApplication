package com.example.bleapplication.utils

object StringUtils {

    fun byteArrayInHexFormat(byteArray: ByteArray) =
        byteArray.joinToString(
            " , ",
            "{ ",
            " }"
        )
        { String.format("%02X", it) }
}