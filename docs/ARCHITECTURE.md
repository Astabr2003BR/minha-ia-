# Arquitetura

## 1. Visão geral

Separar apresentação, domínio, dados e runtimes de IA. Android e Web compartilham contratos de domínio documentados, mas não compartilham diretamente código nativo.

```text
Android (Compose)                 Web (React/PWA)
       |                                |
       +---------- Casos de uso --------+
                       |
             Contratos de domínio
                       |
       +---------------+----------------+
       |               |                |
  Chat/LLM         Documentos       Memória
       |               |                |
  Runtime local    Parser/OCR       Repositório
       |
  Adaptador de modelo
       |
  Arquivos de modelos locais
```

## 2. Android

- Kotlin + Jetpack Compose.
- Arquitetura por módulos: app, core/domain, core/data, feature/chat, feature/manga, feature/documents, feature/memory, ai/runtime.
- SQLiteOpenHelper na primeira versão para histórico e preferências locais, com migrações explícitas e limite de retenção.
- Evoluir para Room quando os testes de build e migração estiverem disponíveis; DataStore poderá assumir preferências simples.
- Android Keystore para proteger chaves de criptografia.
- Modelos armazenados em diretório privado, baixados sob demanda e verificados por hash.
- Runtime inicial a avaliar: llama.cpp com build Android e backend realmente suportado pelo dispositivo.

## 3. Web

- React + TypeScript + PWA.
- IndexedDB versionado para conversas e preferências; operações de escrita serializadas.
- Service Worker para cache do shell da aplicação.
- Inferência no navegador somente se um runtime WebGPU/WebAssembly for compatível; caso contrário, a Web será cliente de um serviço local/remoto explicitamente configurado.
- Não assumir que o navegador consegue acessar os modelos ou a memória privada do APK.

## 4. Contratos de domínio

Definir interfaces independentes de runtime:
- ChatEngine: carregar modelo, gerar tokens, cancelar, descarregar e reportar estado.
- ImageEngine: validar modelo, gerar, cancelar e reportar progresso.
- DocumentProcessor: extrair texto, segmentar e indexar localmente.
- MemoryRepository: salvar, consultar, atualizar, exportar e excluir.
- ProjectRepository: projetos, personagens, cenários, páginas e versões.

## 5. Modelos

Não fixar um modelo único no código. Manter catálogo com:
- Identificador e versão.
- Formato e quantização.
- Tamanho em disco e memória estimada.
- Modalidades suportadas.
- Licença e origem.
- Hash SHA-256.
- Runtime e hardware compatíveis.

Modelo de texto e modelo de imagem são componentes separados. Não presumir que um modelo de texto gera imagens.

## 6. Sincronização

Fora do caminho crítico do modo offline. Futuro serviço deverá:
- Autenticar cada usuário.
- Aplicar autorização no servidor.
- Isolar dados por usuário e dispositivo.
- Usar TLS, criptografia em repouso, versionamento e resolução de conflitos.
- Ser opt-in, com exportação e exclusão de dados.

## 7. Restrições

- Não prometer inteligência equivalente a serviços de grande escala.
- Não prometer desempenho constante no POCO X6 5G antes de benchmark.
- Não executar código, macros ou instruções encontradas em documentos.
- Não incluir credenciais, tokens ou pesos de modelos no Git.


## 8. Estado de implementação da primeira persistência
- Android: SQLite local com tabela de mensagens, limite de 300 itens, limite de 10.000 caracteres, transação ao inserir e exclusão explícita do histórico.
- Web: IndexedDB com stores separados para conversas e preferências, validação de formato, fila serializada de gravações e exclusão do histórico com confirmação.
- A opção de memória apenas registra consentimento/preferência para o recurso futuro. Ela ainda não cria, resume ou recupera memórias.
- O histórico do chat não é apagado ao desativar a preferência de memória.
- Nenhuma sincronização entre Android e Web foi implementada.
