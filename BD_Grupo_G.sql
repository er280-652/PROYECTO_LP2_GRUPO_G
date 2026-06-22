-- Validación de la DB
DROP DATABASE IF EXISTS  gestion_gastos_db;
-- Creación de la DB
CREATE DATABASE gestion_gastos_db;
-- Uso de la DB
USE gestion_gastos_db;

select * from tbl_rol;
-- Creación de Tablas --
-- Tbl Rol
CREATE TABLE tbl_rol(
    id_tipo INT PRIMARY KEY,
    descripcion VARCHAR(20) NOT NULL
);
select * from tbl_usuario;
-- Tbl Usuario
create table tbl_usuario (
	id_usuario int auto_increment primary key,
	nombres varchar(15) not null,
	apellidos varchar(25) not null,
	cuenta varchar(45) not null unique,
	clave varchar(20) not null,
	fecha_nac date,
	activo bit default 1,
	id_tipo int default 2 check(id_tipo in (1,2)), 
	constraint fk_usuario_tipo foreign key (id_tipo) references tbl_rol(id_tipo)
);
insert into tbl_usuario values (null, 'Admin', 'Admin','admin@ciberfarma.com', 'admin', curdate(), default, 1);
insert into tbl_usuario values (null, 'Tito', 'Ciber', 'U001@dominio.com', '10001', curdate(), 0, default);
insert into tbl_usuario values (null, 'Zoila', 'Baca', 'U002@dominio.com', '10002', curdate(), default, default);
insert into tbl_usuario values (null, 'Willy', 'Wonka', 'U003@dominio.com', '10003', curdate(), default, default);
insert into tbl_usuario values (null, 'Charlie', 'Bucket', 'U004@dominio.com', '10004', curdate(), default, default);
insert into tbl_usuario values (null, 'Miranda', 'Rodrigo', 'ro@gmail.com', 'rodrigo', curdate(), default, default);
--  Tbl Categoria
create table tbl_categoria(
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(50) NOT NULL,
    tipo VARCHAR(10) NOT NULL
);
-- Tbl Movimiento
create table tbl_movimiento(
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    descripcion VARCHAR(200) NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    id_categoria INT NOT NULL,
    id_usuario INT NOT NULL,

    CONSTRAINT fk_mov_categoria FOREIGN KEY(id_categoria) REFERENCES tbl_categoria(id_categoria),
    CONSTRAINT fk_mov_usuario FOREIGN KEY(id_usuario) REFERENCES tbl_usuario(id_usuario)
);
-- Tbl Metas
CREATE TABLE tbl_meta(
    id_meta INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(250),
    monto_objetivo DECIMAL(10,2) NOT NULL,
    monto_actual DECIMAL(10,2) DEFAULT 0,
    fecha_objetivo DATE NOT NULL,
    completada BIT DEFAULT 0,
    id_usuario INT NOT NULL,

    CONSTRAINT fk_meta_usuario FOREIGN KEY(id_usuario) REFERENCES tbl_usuario(id_usuario)
);
-- Tbl Presupuesto
CREATE TABLE tbl_presupuesto(
    id_presupuesto INT AUTO_INCREMENT PRIMARY KEY,
    mes varchar(30) NOT NULL,
    anio INT NOT NULL,
    monto_total DECIMAL(10,2) NOT NULL,
    id_usuario INT NOT NULL,

    CONSTRAINT fk_presupuesto_usuario FOREIGN KEY(id_usuario) REFERENCES tbl_usuario(id_usuario)
);
-- Tbl Presupuesto por Categoria
CREATE TABLE tbl_presupuesto_categoria(
    id_presupuesto_categoria INT AUTO_INCREMENT PRIMARY KEY,
    monto_asignado DECIMAL(10,2) NOT NULL,
    id_presupuesto INT NOT NULL,
    id_categoria INT NOT NULL,

    CONSTRAINT fk_pc_presupuesto FOREIGN KEY(id_presupuesto) REFERENCES tbl_presupuesto(id_presupuesto),
    CONSTRAINT fk_pc_categoria FOREIGN KEY(id_categoria) REFERENCES tbl_categoria(id_categoria)
);


-- Indices --
CREATE INDEX idx_mov_usuario ON tbl_movimiento(id_usuario);
CREATE INDEX idx_mov_categoria ON tbl_movimiento(id_categoria);
CREATE INDEX idx_meta_usuario ON tbl_meta(id_usuario);
CREATE INDEX idx_presupuesto_usuario ON tbl_presupuesto(id_usuario);



-- Inserts --
-- Insert Tbl Rol 
INSERT INTO tbl_tipo VALUES (1,'administrador');
INSERT INTO tbl_tipo VALUES (2,'usuario');
