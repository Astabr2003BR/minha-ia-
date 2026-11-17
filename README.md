# Minha IA

Assistente pessoal Android + Web, com prioridade para execução local, privacidade e ausência de cobrança por mensagem.

## Objetivo

Construir uma aplicação com:
- Chat com modelos locais.
- Estúdio de mangá e continuidade visual.
- Geração local de imagens.
- Leitura de documentos.
- Memória configurável.
- Aplicativo Android e interface Web/PWA.
- Sincronização opcional, sem dependência de nuvem para o modo local.

## Princípios

1. **Sem créditos por mensagem:** inferência local; não prometer recursos de nuvem ilimitados.
2. **Offline-first:** chat, memória e arquivos locais devem funcionar sem internet depois da instalação dos modelos.
3. **Privacidade por padrão:** dados privados permanecem no dispositivo, salvo sincronização explicitamente ativada.
4. **Modelos substituíveis:** cada motor deve ter adaptadores independentes.
5. **Preservação:** alterações em personagens, páginas e documentos devem ser versionadas e reversíveis.
6. **Transparência:** mostrar modelo ativo, memória/armazenamento usados e limitações do dispositivo.

## Plataformas

- Android: Kotlin + Jetpack Compose; inferência local via runtime compatível.
- Web: React + TypeScript + PWA; capacidades locais condicionadas ao navegador.
- Dados Android: Room/SQLite e arquivos privados.
- Dados Web: IndexedDB.
- Sincronização: fase posterior, opcional e protegida por autenticação.

## Estado

Planejamento e documentação inicial. Nenhum APK, site publicado ou modelo integrado nesta etapa.

Consulte:
- [Arquitetura](docs/ARCHITECTURE.md)
- [Roadmap](docs/ROADMAP.md)
- [Segurança e privacidade](docs/SECURITY.md)
- [Testes e critérios de aceite](docs/TESTING.md)

## Licenças

Antes de distribuir qualquer modelo, biblioteca ou aplicativo, verificar sua licença, atribuições, restrições comerciais e termos de redistribuição. Não incluir pesos de modelos no repositório sem autorização.
