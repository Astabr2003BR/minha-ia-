import { useEffect, useState, type FormEvent } from "react";
import {
  CHAT_MESSAGE_LIMIT,
  clearChatMessages,
  loadChatMessages,
  loadMemoryEnabled,
  saveChatMessages,
  saveMemoryEnabled,
  type ChatMessage,
} from "./lib/chatStorage";

type Feature = "Chat" | "Mangá" | "Documentos" | "Memória";
const features: Feature[] = ["Chat", "Mangá", "Documentos", "Memória"];

function createMessageId(): string {
  return globalThis.crypto?.randomUUID?.() ??
    `${Date.now()}-${Math.random().toString(36).slice(2)}`;
}

export default function App() {
  const [active, setActive] = useState<Feature>("Chat");
  const [draft, setDraft] = useState("");
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [storageReady, setStorageReady] = useState(false);
  const [canPersist, setCanPersist] = useState(false);
  const [memoryEnabled, setMemoryEnabled] = useState(false);
  const [storageNotice, setStorageNotice] = useState("Carregando dados locais...");

  useEffect(() => {
    let cancelled = false;
    Promise.all([loadChatMessages(), loadMemoryEnabled()])
      .then(([savedMessages, savedMemoryEnabled]) => {
        if (cancelled) return;
        setMessages(savedMessages);
        setMemoryEnabled(savedMemoryEnabled);
        setCanPersist(true);
        setStorageNotice("Histórico e preferências carregados deste navegador.");
      })
      .catch(() => {
        if (!cancelled) {
          setCanPersist(false);
          setStorageNotice("Falha ao carregar os dados locais. O histórico existente não será sobrescrito nesta sessão.");
        }
      })
      .finally(() => {
        if (!cancelled) setStorageReady(true);
      });
    return () => { cancelled = true; };
  }, []);

  useEffect(() => {
    if (!storageReady || !canPersist) return;
    let cancelled = false;
    saveChatMessages(messages).then(
      () => {
        if (!cancelled) setStorageNotice("Histórico salvo neste navegador e dispositivo.");
      },
      () => {
        if (!cancelled) setStorageNotice("Falha ao salvar o histórico local.");
      },
    );
    return () => { cancelled = true; };
  }, [messages, storageReady, canPersist]);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const cleanMessage = draft.trim();
    if (!cleanMessage || cleanMessage.length > CHAT_MESSAGE_LIMIT || !canPersist) return;
    setMessages((current) => [
      ...current,
      { id: createMessageId(), text: cleanMessage, createdAt: Date.now() },
    ].slice(-300));
    setDraft("");
  }

  async function handleMemoryChange(enabled: boolean) {
    const previous = memoryEnabled;
    setMemoryEnabled(enabled);
    try {
      await saveMemoryEnabled(enabled);
      setStorageNotice("Preferência de memória salva.");
    } catch {
      setMemoryEnabled(previous);
      setStorageNotice("Não foi possível salvar a preferência de memória.");
    }
  }

  async function handleClearHistory() {
    if (!globalThis.confirm("Apagar todas as mensagens salvas neste navegador? Esta ação não pode ser desfeita.")) return;
    try {
      await clearChatMessages();
      setMessages([]);
      setStorageNotice("Histórico apagado deste navegador.");
    } catch {
      setStorageNotice("Não foi possível apagar o histórico local.");
    }
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
                    O histórico é persistido localmente. O motor de IA ainda será
                    integrado; por enquanto, não há respostas automáticas.
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
                placeholder={canPersist ? "Escreva uma mensagem..." : "Armazenamento indisponível"}
                value={draft}
                maxLength={CHAT_MESSAGE_LIMIT}
                disabled={!canPersist}
                onChange={(event) => setDraft(event.target.value)}
              />
              <button type="submit" disabled={!draft.trim() || !canPersist}>
                Enviar
              </button>
            </form>
            <p className="notice" role="status">{storageNotice}</p>
            <p className="notice">
              Limite local: 300 mensagens. Os dados permanecem neste navegador;
              limpar os dados do site pode apagá-los.
            </p>
            <button className="secondary-action" type="button" onClick={handleClearHistory} disabled={!canPersist || messages.length === 0}>
              Apagar histórico
            </button>
          </>
        ) : active === "Memória" ? (
          <section className="module-placeholder">
            <h2>Memória configurável</h2>
            <label className="memory-setting">
              <input
                type="checkbox"
                checked={memoryEnabled}
                disabled={!canPersist}
                onChange={(event) => void handleMemoryChange(event.target.checked)}
              />
              Permitir que a futura memória inteligente guarde informações
            </label>
            <p>
              Esta preferência fica salva localmente. A memória inteligente ainda
              não está conectada. Desativar esta opção não apaga o histórico do chat.
            </p>
            <p className="notice" role="status">{storageNotice}</p>
          </section>
        ) : (
          <div className="module-placeholder">
            <h2>{active}</h2>
            <p>
              Este módulo será desenvolvido separadamente. Ainda não há leitura
              de documentos, geração de mangá ou geração de imagens nesta versão.
            </p>
          </div>
        )}
      </section>
    </main>
  );
}
