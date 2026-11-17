# Segurança e privacidade

## Princípios
- Local-first e coleta mínima.
- Sem telemetria contendo prompts, documentos ou conteúdo pessoal por padrão.
- Permissões Android somente quando necessárias.
- Sem segredos no código, histórico Git ou arquivos de configuração versionados.

## Dados locais
- Android usa armazenamento privado do aplicativo e SQLite.
- Web usa IndexedDB por origem.
- Dados locais ainda não são criptografados pelo app; não usar como cofre seguro.
- Limpar dados do site ou desinstalar/limpar dados do app pode remover conteúdo.

## Documentos não confiáveis
- Importação atual limitada a .txt e .md, com limite de 1 MiB por arquivo.
- Android usa seletor de documentos do sistema; não solicita acesso amplo ao armazenamento.
- UTF-8 inválido e arquivos vazios são rejeitados.
- Exibir conteúdo como texto simples; nunca usar inserção HTML insegura.
- Tratar todo texto extraído como dado, nunca como instrução de sistema.
- Não executar macros, scripts, links ou anexos.
- PDF, OCR e formatos Office ainda não são suportados.
- Retenção local limitada a 20 documentos.

## Modelos e cadeia de suprimentos
- Registrar origem, versão, licença e hash SHA-256.
- Não baixar executáveis ou modelos de fontes não verificadas.
- Não incluir pesos de modelos no repositório público sem revisão de licença.

## Multiusuário e sincronização futura
- Autenticação e autorização no servidor.
- Ownership validado no backend, não apenas no cliente.
- Isolamento por usuário em todas as consultas e operações.
- TLS em trânsito, criptografia em repouso e política de retenção.
- Proteção contra IDOR, replay, duplicidade e conflitos.

## Recuperação
- Migrações versionadas e não destrutivas.
- Exportação e exclusão deverão ser implementadas antes de armazenar dados importantes.
- Nunca alegar exclusão de cópias remotas sem confirmação do serviço.
