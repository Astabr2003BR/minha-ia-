# Roadmap

## Fase 0 — Fundação documental
- [x] Registrar objetivo, princípios e escopo.
- [x] Definir arquitetura Android-first e Web.
- [x] Definir requisitos iniciais de segurança e testes.
- [x] Confirmar configuração informada do POCO X6 5G: 8 GB RAM / 256 GB armazenamento.
- [x] Definir primeiro candidato de benchmark (Qwen3-0.6B + LiteRT-LM 0.18.0), sem aprovação de compatibilidade.
- [ ] Medir armazenamento livre, Android/HyperOS e RAM disponível durante execução.

## Fase 1 — Esqueleto executável
- [x] Criar projeto Android Kotlin/Compose (build ainda precisa ser confirmado após correção de dependências).
- [x] Criar projeto Web React/TypeScript/Vite.
- [ ] Configurar lint e formatação.
- [x] Criar testes unitários iniciais do contrato ChatEngine (execução ainda pendente).
- [x] Criar navegação e telas iniciais de Chat, Mangá, Documentos e Memória.
- [x] Adicionar armazenamento local mínimo (SQLite Android e IndexedDB Web).
- [x] Importar TXT/Markdown com validação e limites locais.
- [ ] Executar e aprovar build Android após correção.
- [ ] Executar testes de runtime Android, Web e regressão em dispositivo físico.

## Fase 2 — Chat local
- [ ] Fixar e validar a dependência LiteRT-LM Android 0.18.0 no build.
- [ ] Implementar catálogo e download verificável do candidato Qwen3-0.6B.
- [ ] Implementar inicialização em background, streaming, cancelamento e descarregamento.
- [ ] Tratar pressão de memória, falta de espaço e falha de hash sem perder histórico.
- [ ] Benchmark no POCO X6 5G com teste de qualidade em português.

## Fase 3 — Memória
- [ ] Memória desligável, inspecionável e editável.
- [ ] Separação por perfil/projeto.
- [ ] Recuperação contextual transparente.
- [ ] Exportação e exclusão.

## Fase 4 — Documentos
- [x] Importar TXT/Markdown localmente.
- [ ] Adicionar exclusão de documentos e limpeza dos índices derivados.
- [ ] Segmentação e indexação local.
- [ ] Perguntas com referências aos trechos de origem.
- [ ] PDF/OCR opcional, após testes de parser, limites e segurança.

## Fase 5 — Imagens
- [ ] Avaliar runtime Android compatível.
- [ ] Testar modelo de imagem e licença.
- [ ] Geração individual com cancelamento.
- [ ] Armazenamento e metadados.
- [ ] Testar temperatura, RAM, bateria e falhas.

## Fase 6 — Estúdio de mangá
- [ ] Assistente de roteiro e planejamento de quadros offline.
- [ ] Fichas de personagens, referências e locks aprovados.
- [ ] Cenários, poses e expressões.
- [ ] Montagem de páginas e lettering.
- [ ] Versionamento, comparação e aprovação humana.

## Fase 7 — Web e sincronização
- [ ] PWA com experiência offline limitada por capacidades do navegador.
- [ ] Runtime de inferência local WebGPU/WASM.
- [ ] Importar/exportar backup.
- [ ] Sincronização opcional com autenticação e isolamento.
- [ ] Testes de conflito e recuperação.

## Fase 8 — Release
- [ ] Auditoria de segurança e licenças.
- [ ] Testes de regressão.
- [ ] Build assinado somente com autorização explícita.
- [ ] Publicação somente com autorização explícita.

## Regra de avanço

Nenhuma fase será marcada como concluída sem evidência de build/teste aplicável. Itens não executados permanecem pendentes.
