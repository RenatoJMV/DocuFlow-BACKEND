@RestController
@RequestMapping("/users")
class UserManagementController(
    private val userRepository: UserRepository
) {
    // Listar roles disponibles
    @GetMapping("/roles")
    fun getRoles(): List<String> = listOf("admin", "colaborador")

    // Cambiar el rol de un usuario
    @PutMapping("/{id}/role")
    fun setUserRole(@PathVariable id: Long, @RequestBody body: Map<String, String>): ResponseEntity<User> {
        val user = userRepository.findById(id).orElseThrow()
        val newRole = body["role"] ?: return ResponseEntity.badRequest().build()
        user.role = newRole
        userRepository.save(user)
        return ResponseEntity.ok(user)
    }

    // Obtener permisos de un usuario
    @GetMapping("/{id}/permissions")
    fun getUserPermissions(@PathVariable id: Long): Set<String> {
        val user = userRepository.findById(id).orElseThrow()
        return user.permissions
    }

    // Actualizar permisos de un usuario
    @PutMapping("/{id}/permissions")
    fun setUserPermissions(@PathVariable id: Long, @RequestBody body: Map<String, List<String>>): ResponseEntity<User> {
        val user = userRepository.findById(id).orElseThrow()
        val newPerms = body["permissions"] ?: return ResponseEntity.badRequest().build()
        user.permissions = newPerms.toSet()
        userRepository.save(user)
        return ResponseEntity.ok(user)
    }
}