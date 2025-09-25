CREATE TABLE IF NOT EXISTS Usuario (
    cpf TEXT PRIMARY KEY,
    nome TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    cargo TEXT,
    login TEXT UNIQUE NOT NULL,
    senha TEXT NOT NULL,
    perfil TEXT NOT NULL,
    githuburl TEXT,
    telefonewhatsapp TEXT,
    curso TEXT
);

CREATE TABLE IF NOT EXISTS Equipe (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL UNIQUE,
    descricao TEXT
);

CREATE TABLE IF NOT EXISTS Equipe_Membros (
    equipe_id INTEGER NOT NULL,
    usuario_cpf TEXT NOT NULL,
    PRIMARY KEY (equipe_id, usuario_cpf),
    FOREIGN KEY (equipe_id) REFERENCES Equipe(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_cpf) REFERENCES Usuario(cpf) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Projeto (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    descricao TEXT,
    data_inicio INTEGER NOT NULL,
    data_termino_prevista INTEGER NOT NULL,
    status TEXT NOT NULL,
    gerente_cpf TEXT NOT NULL,
    FOREIGN KEY(gerente_cpf) REFERENCES Usuario(cpf) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS Projeto_Equipes (
    projeto_id INTEGER NOT NULL,
    equipe_id INTEGER NOT NULL,
    PRIMARY KEY (projeto_id, equipe_id),
    FOREIGN KEY (projeto_id) REFERENCES Projeto(id) ON DELETE CASCADE,
    FOREIGN KEY (equipe_id) REFERENCES Equipe(id) ON DELETE CASCADE
);

-- Usuário admin padrão
INSERT OR IGNORE INTO Usuario (cpf, nome, email, cargo, login, senha, perfil, githuburl, telefonewhatsapp, curso) VALUES (
	'00000000191',
	'Administrador',
	'admin@admin.com',
	'Administrador',
	'admin',
	'1234',
    'administrador',
    null,
    null,
    null
);