package cat.copernic.amayo.frontend.core.auth

data class SessionUser(
    val email: String,
    val isConnected: Boolean,
)
