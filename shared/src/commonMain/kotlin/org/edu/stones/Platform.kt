package org.edu.stones

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform