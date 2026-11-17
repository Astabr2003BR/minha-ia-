import { useState, type FormEvent } from "react";

type Feature = "Chat" | "Mangá" | "Documentos" | "Memória";

const features: Feature[] = ["Chat", "Mangá", "Documentos", "Memória"];

export default function App() {
  const [active, setActive] = useState<Feature>("Chat");
  const [draft, setDraft] = useState("");
  const [messages, setMessages] = useState<string[]>([]);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const cleanMessage = draft.trim();
    if (!cleanMessage) return;

    setMessages((current) => [...current, cleanMessage]);
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
                    A interface já recebe suas mensagens. O modelo de IA local
                    ainda será integrado; por enquanto, não há respostas automáticas.
                  </p>
                </div>
              ) : (
                <ol className="messages">
                  {messages.map((message, index) => (
                    <li className="message" key={index}>
                      <span className="message-label">Você</span>
                      <p>{message}</p>
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
                onChange={(event) => setDraft(event.target.value)}
              />
              <button type="submit" disabled={!draft.trim()}>
                Enviar
              </button>
            </form>
            <p className="notice">
              Protótipo: mensagens mantidas somente nesta sessão; ainda sem motor de IA.
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
