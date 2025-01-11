DROP TABLE IF EXISTS Aluno_UC;
DROP TABLE IF EXISTS Alunos;
DROP TABLE IF EXISTS Horario_Turno;
DROP TABLE IF EXISTS Turno;
DROP TABLE IF EXISTS UC;
DROP TABLE IF EXISTS Sala;
DROP TABLE IF EXISTS Curso;
DROP TABLE IF EXISTS Diretores;
DROP TABLE IF EXISTS Horario;
DROP TABLE IF EXISTS Turno;


-- Tabela para Diretores
CREATE TABLE Diretores (
    id VARCHAR(20) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

-- Tabela para Sala
CREATE TABLE Sala (
    referencia VARCHAR(20) PRIMARY KEY,
    capacidade INT NOT NULL
);

-- Tabela para Curso
CREATE TABLE Curso (
    id VARCHAR(20) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    diretorcurso_fk VARCHAR(20),
    FOREIGN KEY (diretorcurso_fk) REFERENCES Diretores(id) ON DELETE SET NULL
);

-- Tabela para UC (Unidade Curricular)
CREATE TABLE UC (
    id VARCHAR(50) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    preferencia VARCHAR(50),
    ano INT NOT NULL,
    opcional BOOLEAN NOT NULL,
    idCurso_fk VARCHAR(100),
    FOREIGN KEY (idCurso_fk) REFERENCES Curso(id) ON DELETE CASCADE
);

-- Tabela para Turno
CREATE TABLE Turno (
    id INT AUTO_INCREMENT PRIMARY KEY,
    inicio TIME NOT NULL,
    fim TIME NOT NULL,
    num INT NOT NULL,
    dia INT NOT NULL,
    tipo INT CHECK (tipo IN (1, 2, 3)), -- 1 - T, 2 - TP, 3 - PL
    limite INT NOT NULL,
    salaReferencia_fk VARCHAR(20),
    idUC_fk VARCHAR(20),
    FOREIGN KEY (salaReferencia_fk) REFERENCES Sala(referencia) ON DELETE SET NULL,
    FOREIGN KEY (idUC_fk) REFERENCES UC(id) ON DELETE CASCADE
);

-- Tabela para Horario
CREATE TABLE Horario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    publicado BOOLEAN DEFAULT FALSE
);

-- Tabela para Alunos
CREATE TABLE Alunos (
    id VARCHAR(20) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NULL,
    estatuto BOOLEAN NOT NULL,
    genero BOOLEAN NOT NULL,
    idHorario_fk INT,
    idCurso_fk VARCHAR(20),
    FOREIGN KEY (idHorario_fk) REFERENCES Horario(id) ON DELETE SET NULL,
    FOREIGN KEY (idCurso_fk) REFERENCES Curso(id) ON DELETE CASCADE
);

-- Tabela para associação Aluno-UC
CREATE TABLE Aluno_UC (
    idAluno_fk VARCHAR(20),
    idUC_fk VARCHAR(20),
    PRIMARY KEY (idAluno_fk, idUC_fk),
    FOREIGN KEY (idAluno_fk) REFERENCES Alunos(id) ON DELETE CASCADE,
    FOREIGN KEY (idUC_fk) REFERENCES UC(id) ON DELETE CASCADE
);

-- Tabela para Horario-Turno
CREATE TABLE Horario_Turno (
    idHorario_fk INT,
    idTurno_fk INT,
    PRIMARY KEY (idHorario_fk, idTurno_fk),
    FOREIGN KEY (idHorario_fk) REFERENCES Horario(id) ON DELETE CASCADE,
    FOREIGN KEY (idTurno_fk) REFERENCES Turno(id) ON DELETE CASCADE
);





