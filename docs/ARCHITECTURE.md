# Arquitetura

## 1. Visão geral

Separar apresentação, domínio, dados e runtimes de IA. Android e Web compartilham contratos de domínio documentados, mas não compartilham diretamente código nativo.

Android (Compose) e Web (React/PWA) usam persistência local independente. Contratos futuros separam ChatEngine, ImageEngine, DocumentProcessor, MemoryRepository e ProjectRepository.

## 2. Android
- Kotlin + Jetpack Compose.
- SQLiteOpenHelper com migrações explícitas: mensagens, preferências e documentos locais.
- Importação de .txt/.md pelo Android Storage Access Framework, sem permissão ampla de armazenamento.
- Leitura UTF-8, até 1 MiB por arquivo e 20 documentos.
- Modelo local futuro: avaliar llama.cpp/runtime compatível somente após benchmark no aparelho.

## 3. Web
- React + TypeScript + Vite.
- IndexedDB versão 3 com stores de conversas, preferências e documentos.
- Importação .txt/.md via input file, validação UTF-8 e limite de 1 MiB por arquivo.
- Documentos são apresentados como texto simples; conteúdo nunca é injetado como HTML.
- Inferência no navegador somente se runtime WebGPU/WebAssembly for compatível; não presumir acesso aos dados privados do APK.

## 4. Contratos futuros
- ChatEngine: carregar modelo, gerar tokens, cancelar, descarregar e reportar estado.
- ImageEngine: validar modelo, gerar, cancelar e reportar progresso.
- DocumentProcessor: extrair texto, segmentar e indexar localmente.
- MemoryRepository: salvar, consultar, atualizar, exportar e excluir.
- ProjectRepository: projetos, personagens, cenários, páginas e versões.

## 5. Modelos
Não fixar um modelo único no código. Catálogo futuro deve registrar identificador, versão, formato, quantização, tamanho, memória estimada, modalidades, licença, origem, SHA-256, runtime e hardware compatíveis. Modelo de texto e modelo de imagem são componentes separados.

## 6. Segurança e sincronização
- Conteúdo importado é dado não confiável, nunca instrução de sistema.
- Não executar macros, scripts ou links de documentos.
- Sincronização futura exige autenticação, autorização no backend, ownership, TLS, criptografia em repouso, versionamento e resolução de conflitos.
- Não incluir credenciais, tokens ou pesos de modelos no Git.

## 7. Limitações atuais
- Sem LLM, geração de imagem, gerador de mangá, OCR, PDF ou busca semântica.
- Memória ainda é somente uma preferência local; não existe memória inteligente.
- Android e Web não sincronizam.
- Dados locais não são criptografados pelo app.
- Desempenho no POCO X6 5G ainda não foi medido.

## 8. Persistência implementada
- Android: SQLite local; histórico até 300 mensagens; documentos .txt/.md até 1 MiB cada, até 20 documentos; migração explícita até schema v3.
- Web: IndexedDB v3; stores separados; gravações serializadas; documentos .txt/.md com limite de 1 MiB e retenção até 20 itens.
- Apagar histórico do chat não apaga documentos.
