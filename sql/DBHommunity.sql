-- Definiciones de tipos ENUM para PostgreSQL
CREATE TYPE tipo_acceso AS ENUM ('entrada', 'salida');

-- Tabla: Zona
CREATE TABLE Zona (
    id_zona INT GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR(100) NOT NULL,
    codigo_postal INT NOT NULL,
    municipiO TEXT NOT NULL,
    colonia TEXT NOT NULL,
    PRIMARY KEY (id_zona)
);

-- Tabla: Rol
CREATE TABLE Rol (
    id_rol INT GENERATED ALWAYS AS IDENTITY,
    nombre_rol VARCHAR(50) UNIQUE,
    PRIMARY KEY (id_rol)
);

-- Tabla: Casa
CREATE TABLE Casa (
    id_casa INT GENERATED ALWAYS AS IDENTITY,
    id_zona INT,
    calle TEXT NOT NULL,
    numero VARCHAR(15) NOT NULL,
    PRIMARY KEY (id_casa),
    UNIQUE (id_zona, calle, numero),
    FOREIGN KEY (id_zona) REFERENCES Zona(id_zona)
);

-- Tabla: Usuario
CREATE TABLE Usuario (
    id_usuario INT GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(100) NOT NULL,
    apellido_materno VARCHAR(100),
    correo VARCHAR(100) NOT NULL UNIQUE,
    contraseña VARCHAR(255) NOT NULL CHECK (
        contraseña ~ '^(?=.*[A-Z])(?=.*[!@#$%^&*()_+{}\[\]:;<>,.?~\\\-=/|]).{8,}$'
    ),
    id_rol INT,
    id_zona INT,
    id_familia INT,
    estado VARCHAR(20) NOT NULL,
    foto_identificacion TEXT,
    PRIMARY KEY (id_usuario),
    FOREIGN KEY (id_rol) REFERENCES Rol(id_rol),
    FOREIGN KEY (id_zona) REFERENCES Zona(id_zona),
    CHECK (
        (id_rol = 1 AND id_familia IS NOT NULL) OR
        (id_rol <> 1)
    ),
    CHECK (estado IN tipo_acceso)
);

-- Tabla: Familia
CREATE TABLE Familia (
    id_familia INT GENERATED ALWAYS AS IDENTITY,
    id_casa INT UNIQUE,
    apellido VARCHAR(100) NOT NULL,
    id_usuario_registrador INT,
    estado VARCHAR(20),
    foto_identificacion TEXT,
    fecha_registro TIMESTAMP NOT NULL,
    PRIMARY KEY (id_familia),
    FOREIGN KEY (id_casa) REFERENCES Casa(id_casa),
    FOREIGN KEY (id_usuario_registrador) REFERENCES Usuario(id_usuario),
    CHECK (estado IS NULL OR estado IN ('pendiente', 'aprobado', 'rechazado'))
);

-- FK para id_familia en Usuario
ALTER TABLE Usuario
ADD CONSTRAINT fk_id_familia
FOREIGN KEY (id_familia) REFERENCES Familia(id_familia);

-- Tabla: Invitado
CREATE TABLE Invitado (
    id_invitado INT GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(100) NOT NULL,
    apellido_materno VARCHAR(100),
    fecha_visita DATE NOT NULL,
    fecha_entrada TIMESTAMP NOT NULL,
    fecha_salida TIMESTAMP NOT NULL,
    id_usuario INT,
    PRIMARY KEY (id_invitado),
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
);

-- Tabla: QR (ACTUALIZADA CON id_usuario, FK y CHECK)
CREATE TABLE QR (
    id_qr INT GENERATED ALWAYS AS IDENTITY,
    codigo TEXT NOT NULL UNIQUE,
    id_invitado INT,
    id_usuario INT,
    fecha_creacion TIMESTAMP NOT NULL,
    vigente BOOLEAN DEFAULT TRUE,
    usos_disponibles INT default 1,
    PRIMARY KEY (id_qr),
    FOREIGN KEY (id_invitado) REFERENCES Invitado(id_invitado),
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario),
    CHECK (
        (id_invitado IS NOT NULL AND id_usuario IS NULL) OR
        (id_invitado IS NULL AND id_usuario IS NOT NULL)
    )
);

-- Tabla: Acceso
CREATE TABLE Acceso (
    id_acceso INT GENERATED ALWAYS AS IDENTITY,
    id_qr INT,
    tipo tipo_acceso NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    PRIMARY KEY (id_acceso),
    FOREIGN KEY (id_qr) REFERENCES QR(id_qr)
);

-- Restricción adicional para Usuario
ALTER TABLE Usuario
ADD CONSTRAINT chk_id_familia_estado
CHECK ((id_familia IS NULL AND estado = 'pendiente') OR (id_familia IS NOT NULL));

-- Trigger opcional para limitar a 5 miembros por familia
/*
CREATE OR REPLACE FUNCTION limitar_miembros_familia()
RETURNS TRIGGER AS $$
DECLARE
    miembros_actuales INT;
BEGIN
    IF NEW.id_familia IS NOT NULL THEN
        SELECT COUNT(*) INTO miembros_actuales
        FROM Usuario
        WHERE id_familia = NEW.id_familia;

        IF miembros_actuales >= 5 THEN
            RAISE EXCEPTION 'Una familia no puede tener más de 5 miembros.';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_limitar_miembros_familia
BEFORE INSERT OR UPDATE OF id_familia ON Usuario
FOR EACH ROW
EXECUTE FUNCTION limitar_miembros_familia();
*/
