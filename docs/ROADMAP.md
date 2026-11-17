# Roadmap

## Fase 0 — Fundação documental
- [x] Registrar objetivo, princípios e escopo.
- [x] Definir arquitetura Android-first e Web.
- [x] Definir requisitos iniciais de segurança e testes.
- [ ] Confirmar configuração exata do POCO X6 5G (RAM/armazenamento/Android).
- [ ] Escolher licenças e modelos após teste de compatibilidade.

## Fase 1 — Esqueleto executável
- [x] Criar projeto Android Kotlin/Compose (build ainda precisa ser confirmado após correção de dependências).
- [x] Criar projeto Web React/TypeScript/Vite.
- [ ] Configurar lint, formatação e testes automatizados.
- [x] Criar navegação e telas iniciais de Chat, Mangá, Documentos e Memória.
- [x] Adicionar armazenamento local mínimo (SQLite Android e IndexedDB Web).
- [x] Importar TXT/Markdown com validação e limites locais.
- [ ] Executar e aprovar build Android após correção.
- [ ] Executar testes de runtime Android, Web e regressão em dispositivo físico.

## Fase 2 — Chat local
- [ ] Fixar versões testadas do runtime Android e Web.
- [ ] Catálogo e download verificável de modelos.
- [ ] Streaming, cancelamento e tratamento de falta de memória.
- [ ] Histórico e exclusão de conversas.
- [ ] Benchmark no POCO X6 5G.

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
