# Segurança e privacidade

## Princípios
- Local-first e coleta mínima.
- Sem telemetria contendo prompts, documentos ou conteúdo pessoal por padrão.
- Permissões Android solicitadas somente quando necessárias.
- Sem segredos no código, histórico Git ou arquivos de configuração versionados.

## Dados locais
- Usar armazenamento privado do aplicativo.
- Criptografar backups e dados sensíveis quando aplicável.
- Proteger chaves com Android Keystore.
- Não registrar conteúdo de conversas em logs.
- Validar integridade de modelos baixados com SHA-256.

## Documentos não confiáveis
- Tratar todo texto extraído como dado, nunca como instrução de sistema.
- Não executar macros, scripts, links ou anexos.
- Limitar tamanho, páginas, tempo de processamento e memória.
- Tratar arquivos malformados sem travar nem sobrescrever dados.
- Exibir origem/página de trechos usados nas respostas.

## Multiusuário e sincronização futura
- Autenticação no servidor.
- Autorização e ownership validados no backend, não apenas no cliente.
- Isolamento por usuário em todas as consultas e operações.
- TLS em trânsito.
- Criptografia em repouso e política de retenção.
- Proteção contra IDOR, replay, duplicidade e conflitos.
- Logout deve limpar credenciais locais; dados locais seguem política explícita de retenção.

## Modelos e cadeia de suprimentos
- Registrar origem, versão, licença e hash.
- Não baixar executáveis ou modelos de fontes não verificadas.
- Não carregar modelos com formatos que permitam execução arbitrária sem revisão.
- Atualizações devem ser verificáveis e reversíveis.

## Incidentes e recuperação
- Backup antes de migrações destrutivas.
- Migrações versionadas e testadas.
- Exportação e exclusão acessíveis ao usuário.
- Nunca alegar exclusão de cópias remotas sem confirmação do serviço.
