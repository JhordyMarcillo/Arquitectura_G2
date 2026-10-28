<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Monsters Inc. Conversiones - Login</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --monster-blue: #21a9da;
            --monster-purple: #8c6bcd;
            --monster-pink: #f66971;
            --monster-yellow: #f5d880;
            --monster-light-blue: #9fdcfa;
            --monster-dark: #2c3e50;
            --monster-gray: #ecf0f1;
            --monster-white: #ffffff;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f5f5;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .login-container {
            background-color: var(--monster-white);
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 1000px;
            min-height: 600px;
            display: flex;
            overflow: hidden;
        }

        /* Left Panel - Branding */
        .branding-panel {
            flex: 2;
            background-color: #21a9da;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 40px;
            position: relative;
        }

        .logo-container {
            position: relative;
            z-index: 2;
            text-align: center;
            margin-bottom: 30px;
        }

        .logo-container img {
            width: 150px;
            height: 150px;
            object-fit: contain;
            margin: 0 auto 20px;
            display: block;
            border-radius: 10px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        }

        .branding-text {
            color: #ffffff;
            text-align: center;
            position: relative;
            z-index: 2;
        }

        .branding-subtitle {
            font-size: 1.2rem;
            font-weight: 300;
            margin-bottom: 10px;
            opacity: 0.9;
        }

        .branding-title {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 15px;
            color: #ffffff;
        }

        .branding-description {
            font-size: 1rem;
            opacity: 0.9;
            margin-bottom: 20px;
            color: #ffffff;
        }

        .version-info {
            font-size: 0.9rem;
            opacity: 0.8;
            position: absolute;
            bottom: 30px;
            left: 50%;
            transform: translateX(-50%);
            color: #ffffff;
        }

        /* Right Panel - Login Form */
        .form-panel {
            flex: 1;
            background-color: var(--monster-gray);
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 40px;
        }

        .form-title {
            font-size: 2rem;
            font-weight: 700;
            color: var(--monster-blue);
            margin-bottom: 30px;
            text-align: center;
        }

        .form-container {
            width: 100%;
            max-width: 300px;
        }

        .form-group {
            margin-bottom: 25px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: var(--monster-dark);
            font-size: 1rem;
        }

        .form-group input {
            width: 100%;
            padding: 15px 20px;
            border: 2px solid #ddd;
            border-radius: 10px;
            font-size: 1rem;
            transition: all 0.3s ease;
            background-color: var(--monster-white);
            color: var(--monster-dark);
        }

        .form-group input:focus {
            outline: none;
            border-color: var(--monster-blue);
            box-shadow: 0 0 0 3px rgba(33, 169, 218, 0.1);
        }

        .button-group {
            display: flex;
            gap: 15px;
            margin-bottom: 20px;
        }

        .btn {
            flex: 1;
            padding: 15px 20px;
            border: none;
            border-radius: 10px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-transform: uppercase;
        }

        .btn-primary {
            background-color: var(--monster-blue);
            color: var(--monster-white);
        }

        .btn-primary:hover {
            background-color: var(--monster-purple);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(33, 169, 218, 0.3);
        }

        .btn-secondary {
            background-color: var(--monster-pink);
            color: var(--monster-white);
        }

        .btn-secondary:hover {
            background-color: #e55a61;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(246, 105, 113, 0.3);
        }

        .btn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }

        .error-message {
            background-color: #ffebee;
            border: 1px solid #ffcdd2;
            color: #c62828;
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 20px;
            display: none;
            text-align: center;
        }

        .loading {
            display: none;
            text-align: center;
            margin-top: 15px;
        }

        .spinner {
            display: inline-block;
            width: 20px;
            height: 20px;
            border: 2px solid #ddd;
            border-radius: 50%;
            border-top-color: var(--monster-blue);
            animation: spin 1s ease-in-out infinite;
        }

        @keyframes spin {
            to { transform: rotate(360deg); }
        }

        .instruction-text {
            text-align: center;
            color: var(--monster-dark);
            font-size: 0.9rem;
            opacity: 0.7;
        }

        /* Responsive Design */
        @media (max-width: 768px) {
            .login-container {
                flex-direction: column;
                max-width: 400px;
                min-height: auto;
            }

            .branding-panel {
                flex: none;
                min-height: 300px;
            }

            .form-panel {
                flex: none;
                padding: 30px 20px;
            }

            .branding-title {
                font-size: 2rem;
            }

            .form-title {
                font-size: 1.5rem;
            }
        }
    </style>
</head>
<body>
    <div class="login-container">
        <!-- Left Panel - Branding -->
        <div class="branding-panel">
            <div class="logo-container">
                <img src="resources/images/logo.png" alt="Monsters Inc. Logo" />
            </div>
            <div class="branding-text">
                <h1 class="branding-title">MONSTERS INC.</h1>
                <p class="branding-description">Sistema de Conversiones</p>
            </div>
            <div class="version-info">Edición Monstruosa v1.0</div>
        </div>

        <!-- Right Panel - Login Form -->
        <div class="form-panel">
            <h2 class="form-title">LOGUÉATE</h2>
            
            <div class="error-message" id="errorMessage"></div>
            
            <form id="loginForm" class="form-container">
                <div class="form-group">
                    <label for="usuario">Usuario:</label>
                    <input type="text" id="usuario" name="usuario" value="MONSTER" required>
                </div>
                
                <div class="form-group">
                    <label for="contrasena">Contraseña:</label>
                    <input type="password" id="contrasena" name="contrasena" value="MONSTER9" required>
                </div>
                
                <div class="button-group">
                    <button type="submit" class="btn btn-primary" id="loginBtn">
                        <i class="fas fa-sign-in-alt"></i> ACCEDER
                    </button>
                    <button type="button" class="btn btn-secondary" onclick="window.close()">
                        <i class="fas fa-times"></i> SALIR
                    </button>
                </div>
                
                <div class="loading" id="loading">
                    <div class="spinner"></div>
                    <span>Verificando credenciales...</span>
                </div>
            </form>
            
            <p class="instruction-text">Ingrese sus credenciales para acceder</p>
        </div>
    </div>

    <script>
        document.getElementById('loginForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const usuario = document.getElementById('usuario').value;
            const contrasena = document.getElementById('contrasena').value;
            const loginBtn = document.getElementById('loginBtn');
            const loading = document.getElementById('loading');
            const errorMessage = document.getElementById('errorMessage');
            
            // Mostrar loading
            loginBtn.disabled = true;
            loading.style.display = 'block';
            errorMessage.style.display = 'none';
            
            try {
                const params = new URLSearchParams();
                params.append('accion', 'login');
                params.append('usuario', usuario);
                params.append('contrasena', contrasena);
                
                const response = await fetch('ControladorWeb', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: params
                });
                
                // Verificar si la respuesta es OK
                if (!response.ok) {
                    const errorText = await response.text();
                    console.error('Error del servidor:', response.status, errorText);
                    errorMessage.textContent = 'Error del servidor: ' + response.status + '. Por favor, intente nuevamente.';
                    errorMessage.style.display = 'block';
                    return;
                }
                
                const data = await response.json();
                console.log('Respuesta del servidor:', data);
                
                if (data.exitoso) {
                    // Redirigir al sistema principal
                    window.location.href = 'index.jsp';
                } else {
                    errorMessage.textContent = data.mensaje || 'Credenciales incorrectas';
                    errorMessage.style.display = 'block';
                }
                
            } catch (error) {
                console.error('Error en la petición:', error);
                errorMessage.textContent = 'Error de conexión: ' + error.message + '. Verifique que el servidor esté funcionando.';
                errorMessage.style.display = 'block';
            } finally {
                loginBtn.disabled = false;
                loading.style.display = 'none';
            }
        });
    </script>
</body>
</html>
