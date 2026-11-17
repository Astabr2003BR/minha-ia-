import { useEffect, useState, type FormEvent } from "react";
import {
  CHAT_MESSAGE_LIMIT,
  loadChatMessages,
  saveChatMessages,
  type ChatMessage,
} from "./lib/chatStorage";

type Feature = "Chat" | "Mangá" | "Documentos" | "Memória";

const features: Feature[] = ["Chat", "Mangá", "Documentos", "Memória"];

function createMessageId(): string {
  return `${Date.now()}-${Math.random().toString(36).slice(2)}`;
}

export default function App() {
  const [active, setActive] = useState<Feature>("Chat");
  const [draft, setDraft] = useState("");
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [storageReady, setStorageReady] = useState(false);
  const [storageNotice, setStorageNotice] = useState("Carregando histórico local...");

  useEffect(() => {
    let cancelled = false;

    loadChatMessages()
      .then((savedMessages) => {
        if (!cancelled) {
          setMessages(savedMessages);
          setStorageNotice("Histórico salvo somente neste navegador e dispositivo.");
        }
      })
      .catch(() => {
        if (!cancelled) {
          setStorageNotice("Não foi possível ler o histórico local. As mensagens desta sessão ainda podem ser usadas, mas não serão persistidas.");
        }
      })
      .finally(() => {
        if (!cancelled) setStorageReady(true);
      });

    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    if (!storageReady) return;

    let cancelled = false;
    saveChatMessages(messages).then(
      () => {
        if (!cancelled) {
          setStorageNotice("Histórico salvo somente neste navegador e dispositivo.");
        }
      },
      () => {
        if (!cancelled) {
          setStorageNotice("Falha ao salvar localmente. O histórico pode se perder ao fechar esta página.");
        }
      },
    );

    return () => {
      cancelled = true;
    };
  }, [messages, storageReady]);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const cleanMessage = draft.trim();
    if (!cleanMessage || cleanMessage.length > CHAT_MESSAGE_LIMIT) return;

    setMessages((current) => [
      ...current,
      { id: createMessageId(), text: cleanMessage, createdAt: Date.now() },
    ].slice(-300));
    setDraft("");
  }

  return (
    <main className="shell">
      <aside className="sidebar" aria-label="Navegação principal">
        <div className="brand">Minha IA</div>
        <nav className="navigation">
          {features.map((item) => (
            <button
              className={active === item ? "nav active" : "nav"}
              key={item}
              onClick={() => setActive(item)}
              type="button"
              aria-current={active === item ? "page" : undefined}
            >
              {item}
            </button>
          ))}
        </nav>
        <p className="muted">Modo local · sem conta</p>
      </aside>

      <section className="workspace">
        <header>
          <span className="eyebrow">ASSISTENTE PESSOAL</span>
          <h1>{active}</h1>
        </header>

        {active === "Chat" ? (
          <>
            <section className="conversation" aria-label="Conversa" aria-live="polite">
              {messages.length === 0 ? (
                <div className="welcome">
                  <h2>O que vamos criar hoje?</h2>
                  <p>
                    O histórico agora pode ser salvo localmente. O modelo de IA
                    ainda será integrado; por enquanto, não há respostas automáticas.
                  </p>
                </div>
              ) : (
                <ol className="messages">
                  {messages.map((message) => (
                    <li className="message" key={message.id}>
                      <span className="message-label">Você</span>
                      <p>{message.text}</p>
                    </li>
                  ))}
                </ol>
              )}
            </section>

            <form className="composer" onSubmit={handleSubmit}>
              <input
                aria-label="Escreva sua mensagem"
                placeholder="Escreva uma mensagem..."
                value={draft}
                maxLength={CHAT_MESSAGE_LIMIT}
                onChange={(event) => setDraft(event.target.value)}
              />
              <button type="submit" disabled={!draft.trim() || draft.trim().length > CHAT_MESSAGE_LIMIT}>
                Enviar
              </button>
            </form>
            <p className="notice" role="status">{storageNotice}</p>
            <p className="notice">
              Limite local: 300 mensagens. Os dados não são enviados para servidores.
              Limpar os dados do navegador pode apagar este histórico.
            </p>
          </>
        ) : (
          <div className="module-placeholder">
            <h2>{active}</h2>
            <p>
              Este módulo será desenvolvido separadamente. Nenhum dado é enviado
              ou salvo nesta versão.
            </p>
          </div>
        )}
      </section>
    </main>
  );
}
