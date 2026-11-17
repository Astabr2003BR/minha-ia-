import { useState } from "react";

type Feature = "Chat" | "Mangá" | "Documentos" | "Memória";

export default function App() {
  const [active, setActive] = useState<Feature>("Chat");
  const [message, setMessage] = useState("");

  return (
    <main className="shell">
      <aside className="sidebar">
        <div className="brand">Minha IA</div>
        {(["Chat", "Mangá", "Documentos", "Memória"] as Feature[]).map((item) => (
          <button
            className={active === item ? "nav active" : "nav"}
            key={item}
            onClick={() => setActive(item)}
            type="button"
          >
            {item}
          </button>
        ))}
        <p className="muted">Modo inicial · sem conta</p>
      </aside>

      <section className="workspace">
        <header>
          <span className="eyebrow">ASSISTENTE PESSOAL</span>
          <h1>{active}</h1>
        </header>

        <div className="welcome">
          <h2>O que vamos criar hoje?</h2>
          <p>
            Esta é a estrutura inicial. A geração local de respostas, imagens e
            documentos será conectada nas próximas etapas.
          </p>
        </div>

        <form
          className="composer"
          onSubmit={(event) => {
            event.preventDefault();
            setMessage("");
          }}
        >
          <input
            aria-label="Escreva sua mensagem"
            placeholder="Escreva uma mensagem..."
            value={message}
            onChange={(event) => setMessage(event.target.value)}
          />
          <button type="submit" disabled={!message.trim()}>
            Enviar
          </button>
        </form>
        <p className="notice">Protótipo de interface: ainda não envia mensagens para um modelo de IA.</p>
      </section>
    </main>
  );
}
