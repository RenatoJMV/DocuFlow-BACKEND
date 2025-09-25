-- Script SQL para insertar datos de prueba en DocuFlow Backend

-- Insertar usuario de prueba (estudiante)
INSERT INTO users (username, password, role) VALUES 
('estudiante', '123456', 'admin')
ON CONFLICT (username) DO NOTHING;

-- Insertar documentos de prueba
INSERT INTO documents (filename, file_type, file_path, size) VALUES 
('documento-prueba.pdf', 'application/pdf', 'uploads/documento-prueba.pdf', 2048576),
('reporte-mensual.xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'uploads/reporte-mensual.xlsx', 1572864),
('imagen-ejemplo.jpg', 'image/jpeg', 'uploads/imagen-ejemplo.jpg', 819200),
('presentacion.pptx', 'application/vnd.openxmlformats-officedocument.presentationml.presentation', 'uploads/presentacion.pptx', 3145728),
('manual-usuario.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'uploads/manual-usuario.docx', 1048576)
ON CONFLICT DO NOTHING;

-- Insertar logs de actividad de prueba
INSERT INTO logs (action, username, document_id, timestamp) VALUES 
('login', 'estudiante', NULL, NOW() - INTERVAL '1 hour'),
('upload', 'estudiante', 1, NOW() - INTERVAL '50 minutes'),
('upload', 'estudiante', 2, NOW() - INTERVAL '45 minutes'),
('download', 'estudiante', 1, NOW() - INTERVAL '30 minutes'),
('upload', 'estudiante', 3, NOW() - INTERVAL '25 minutes'),
('comment', 'estudiante', 1, NOW() - INTERVAL '20 minutes'),
('download', 'estudiante', 2, NOW() - INTERVAL '15 minutes'),
('upload', 'estudiante', 4, NOW() - INTERVAL '10 minutes'),
('upload', 'estudiante', 5, NOW() - INTERVAL '5 minutes'),
('download', 'estudiante', 3, NOW() - INTERVAL '2 minutes')
ON CONFLICT DO NOTHING;

-- Insertar comentarios de prueba
INSERT INTO comments (content, author, document_id, is_task, created_at) VALUES 
('Este documento necesita revisión urgente', 'estudiante', 1, true, NOW() - INTERVAL '20 minutes'),
('Excelente trabajo, aprobado para publicación', 'estudiante', 2, false, NOW() - INTERVAL '15 minutes'),
('Por favor corregir el formato de la página 3', 'estudiante', 1, true, NOW() - INTERVAL '10 minutes'),
('Agregar más datos en la sección de conclusiones', 'estudiante', 4, true, NOW() - INTERVAL '8 minutes'),
('Documento revisado y listo', 'estudiante', 3, false, NOW() - INTERVAL '5 minutes'),
('Pendiente de aprobación del supervisor', 'estudiante', 5, true, NOW() - INTERVAL '3 minutes')
ON CONFLICT DO NOTHING;

-- Insertar asignaciones de tareas
INSERT INTO comment_assignees (comment_id, assignee) VALUES 
(1, 'estudiante'),
(3, 'estudiante'), 
(4, 'estudiante'),
(6, 'estudiante')
ON CONFLICT DO NOTHING;

-- Actualizar permisos del usuario estudiante
UPDATE users SET 
  permissions = ARRAY['download', 'delete', 'comment', 'edit', 'share', 'admin', 'view_logs', 'manage_users']
WHERE username = 'estudiante';