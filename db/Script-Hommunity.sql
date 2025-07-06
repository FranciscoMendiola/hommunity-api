 
-- CREACIÓN DE TABLAS BÁSICAS SIN RELACIONES

-- zona
CREATE TABLE zona (
    id_zona int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    municipio text NOT NULL,
    codigo_postal int4 NOT NULL,
    colonia text NOT null,
    nombre varchar(100) NOT NULL,
    
    
    CONSTRAINT ux_zona_municipio_cp_colonia_nombre UNIQUE (municipio,codigo_postal,colonia,nombre)
);

-- rol
CREATE TABLE rol (
    id_rol int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_rol varchar(50) NOT NULL,
    
    constraint ux_rol_nombre_rol UNIQUE (nombre_rol)
);

-- usuario
CREATE TABLE usuario (
    id_usuario int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre varchar(100) NOT NULL,
    apellido_paterno varchar(100) NOT NULL,
    apellido_materno varchar(100),
    correo varchar(100) NOT NULL,
    contraseña varchar(255) NOT NULL,
    id_rol int4 not null,
    id_zona int4 not null,
    id_familia int4,
    estado varchar(20) NOT NULL,
    foto_identificacion text,
    
    CONSTRAINT chk_usuario_contraseña CHECK (contraseña ~ '^(?=.*[A-Z])(?=.*[!@#$%^&*()_+{}\[\]:;<>,.?~\\\-=/|]).{8,}$'),
    CONSTRAINT chk_usuario_correo CHECK (correo ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_usuario_estado CHECK (estado IN ('PENDIENTE', 'APROBADO')),
    CONSTRAINT ux_usuario_correo UNIQUE (correo)
);

-- casa
CREATE TABLE casa (
    id_casa int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_zona int4 not null,
    calle text NOT NULL,
    numero varchar(15) NOT NULL,
    
    CONSTRAINT ux_casa_id_zona_calle_numero UNIQUE (id_zona, calle, numero)
);

-- familia
CREATE TABLE familia (
    id_familia int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_casa int4 not null,
    apellido varchar(100) NOT NULL,
    id_usuario_registrador int4,
    estado varchar(20) not null,
    foto_identificacion text,
    fecha_registro timestamp NOT NULL,
    
    CONSTRAINT ux_id_casa UNIQUE (id_casa),
    CONSTRAINT chk_familia_estado CHECK (estado IN ('PENDIENTE', 'APROBADO')),
    -- Pendiente (esto limita a que cada usuario tenga como maximo una familia o casa)
    CONSTRAINT ux_familia_usuario_registrador UNIQUE (id_usuario_registrador)
);

-- invitado
CREATE TABLE invitado (
    id_invitado int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre varchar(100) NOT NULL,
    apellido_paterno varchar(100) NOT NULL,
    apellido_materno varchar(100),
    id_usuario int4 not null,
    fecha_entrada timestamp not null,
    fecha_salida timestamp not null
);

-- qr
CREATE TABLE qr (
    id_qr int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo text NOT NULL,
    id_invitado int4,
    fecha_creacion timestamp not null,
    vigente bool DEFAULT true,
    id_usuario int4,
    usos_disponibles int4 DEFAULT 2,
    
    constraint ux_qr_codigo unique (codigo),
    
    CONSTRAINT chk_qr_invitado_o_usuario CHECK (((id_invitado IS NOT NULL AND id_usuario IS NULL) OR
         (id_invitado IS NULL AND id_usuario IS NOT NULL))
    )
);

-- acceso
CREATE TABLE acceso (
    id_acceso int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_qr int4,
    tipo varchar(20) NOT NULL,
    fecha timestamp NOT null,
    
    CONSTRAINT chk_acceso_tipo CHECK (tipo IN ('ENTRADA', 'SALIDA'))
);

-- ===========================================
-- AGREGAR LAS CLAVES FORÁNEAS CON ALTER TABLE
-- ===========================================

-- usuario → rol
ALTER TABLE usuario
ADD CONSTRAINT fk_usuario_id_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol);

-- usuario → zona
ALTER TABLE usuario
ADD CONSTRAINT fk_usuario_id_zona FOREIGN KEY (id_zona) REFERENCES zona(id_zona);

-- usuario → familia
ALTER TABLE usuario
ADD CONSTRAINT fk_usuario_id_familia FOREIGN KEY (id_familia) REFERENCES familia(id_familia);


-- casa → zona
ALTER TABLE casa
ADD CONSTRAINT fk_casa_id_zona FOREIGN KEY (id_zona) REFERENCES zona(id_zona);


-- familia → casa
ALTER TABLE familia
ADD CONSTRAINT fk_familia_id_casa FOREIGN KEY (id_casa) REFERENCES casa(id_casa);

-- familia → usuario (usuario que registró la familia)
ALTER TABLE familia
ADD CONSTRAINT fk_familia_id_usuario_registrador FOREIGN KEY (id_usuario_registrador) REFERENCES usuario(id_usuario);


-- invitado → usuario
ALTER TABLE invitado
ADD CONSTRAINT fk_invitado_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario);


-- qr → invitado
ALTER TABLE qr
ADD CONSTRAINT fk_qr_id_invitado FOREIGN KEY (id_invitado) REFERENCES invitado(id_invitado);

-- qr → usuario
ALTER TABLE qr
ADD CONSTRAINT fk_qr_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario);


-- acceso → qr
ALTER TABLE acceso
ADD CONSTRAINT fk_acceso_id_qr FOREIGN KEY (id_qr) REFERENCES qr(id_qr);
