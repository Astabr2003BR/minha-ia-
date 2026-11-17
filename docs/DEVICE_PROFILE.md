# Perfil-alvo de dispositivo — POCO X6 5G

## Configuração confirmada pelo usuário

- Aparelho: POCO X6 5G
- RAM física: 8 GB
- Armazenamento interno: 256 GB
- Plataforma prioritária: Android
- Web: plataforma complementar, com runtime e armazenamento independentes

A quantidade de armazenamento livre, versão exata do Android/HyperOS e disponibilidade de RAM durante a execução ainda não foram medidas.

## Decisões de engenharia

1. Priorizar inferência local de texto com modelo compacto e quantizado.
2. Não carregar o modelo automaticamente na inicialização do aplicativo.
3. Fazer download somente após ação explícita, com espaço livre verificado, arquivo temporário, SHA-256 e promoção atômica.
4. Executar inicialização e geração fora da thread principal.
5. Começar com contexto e tamanho de resposta conservadores; permitir cancelamento e descarregamento do modelo.
6. Manter geração de imagens separada do chat. Não ativá-la até medir RAM, temperatura, bateria e tempo de geração no aparelho real.
7. Não usar serviço remoto pago como fallback silencioso.

## Primeiro candidato de benchmark (não aprovado para produção)

- Runtime candidato: LiteRT-LM Android Kotlin API.
- Versão inicial a avaliar: `0.18.0`, fixada (sem `latest.release`).
- Modelo: `litert-community/Qwen3-0.6B`.
- Artefato inicial: `Qwen3-0.6B.litertlm` (aprox. 614 MB; variante dinâmica INT8; contexto declarado de 4096 tokens).
- Revisão imutável do repositório do modelo: `e6687177e00897731e7716267eebd4eba0dde7c2`.
- SHA-256 publicado para o artefato: `c5f39b4191926786c3e6668b62af4d6eb15248dfc7a5348252a9b406f33f2108`.
- Licença indicada no model card: Apache-2.0; revisar também as condições do modelo-base e manter atribuições.
- Compatibilidade funcional, qualidade em português e desempenho no POCO X6 5G: ainda não testados.

O artefato INT4 dinâmico de aproximadamente 328 MiB não será o primeiro alvo: o model card informa requisitos de runtime mais recentes para o caminho GPU e diferenças relevantes de memória. Evitar misturar versões de runtime e modelos durante o primeiro benchmark.

## Gate de aprovação do modelo

O candidato só poderá ser aprovado após:
- build Android com a dependência exata;
- download integral e validação do SHA-256;
- inicialização, geração, streaming, cancelamento e descarregamento;
- teste sem internet após o download;
- medição de pico de memória, TTFT, tokens/s, temperatura e bateria;
- teste em português com perguntas de chat, instruções longas e planejamento de mangá;
- repetição após alternar para outros aplicativos e retornar;
- ausência de perda do histórico em falhas do motor.

Se houver encerramento pelo sistema, lentidão excessiva, aquecimento anormal ou pressão de memória, reduzir contexto/geração ou reprovar o modelo. Não presumir que 8 GB de RAM garantem compatibilidade.

## O que ainda falta medir no aparelho

- versão Android/HyperOS;
- armazenamento livre real;
- RAM disponível antes e durante o benchmark;
- ABI e backend efetivamente disponíveis;
- tempo de carregamento e geração no dispositivo.
