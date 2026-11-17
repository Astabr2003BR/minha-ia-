# Testes e critérios de aceite

## Estado atual
Documentação apenas. Nenhum build ou teste de aplicação foi executado nesta etapa.

## Matriz de testes

| Área | Cenário | Resultado esperado |
|---|---|---|
| Chat | Modelo ausente | Mensagem clara; sem crash |
| Chat | Cancelar geração | Geração interrompida e UI recuperada |
| Chat | Memória insuficiente | Falha controlada; histórico preservado |
| Modelos | Download interrompido | Retomar ou descartar arquivo parcial com segurança |
| Modelos | Hash incorreto | Recusar carregamento |
| Memória | Desativar memória | Não recuperar memórias pessoais |
| Memória | Excluir conversa | Remover registros relacionados conforme política |
| Documentos | Arquivo vazio/corrompido | Rejeitar com erro compreensível |
| Documentos | PDF grande | Aplicar limites sem bloquear a interface |
| Documentos | Prompt injection no texto | Tratar conteúdo como dado não confiável |
| Usuários | Trocar conta | Não misturar dados |
| Armazenamento | Pouco espaço | Não corromper banco nem substituir arquivos |
| Offline | Sem internet | Recursos locais continuam disponíveis |
| Web | Cache incompleto | Informar limitação e recuperar shell quando possível |
| Sincronização | Reenvio duplicado | Operação idempotente |
| Sincronização | Conflito entre dispositivos | Resolver sem perda silenciosa |

## Critérios de release
- Build reproduzível.
- Testes unitários e de integração aprovados.
- Nenhum segredo no repositório.
- Verificação de dependências e licenças.
- Teste em dispositivo físico.
- Testes de regressão.
- Evidências registradas por versão.
