import { useEffect, useRef, useState, type ChangeEvent, type FormEvent } from "react";
import {
  CHAT_MESSAGE_LIMIT,
  DOCUMENT_BYTE_LIMIT,
  clearChatMessages,
  loadChatMessages,
  loadDocuments,
  loadMemoryEnabled,
  saveChatMessages,
  saveDocument,
  saveMemoryEnabled,
  type ChatMessage,
  type LocalDocument,
} from "./lib/chatStorage";

type Feature = "Chat" | "Mangá" | "Documentos" | "Memória";
const features: Feature[] = ["Chat", "Mangá", "Documentos", "Memória"];

function createId(): string {
  return globalThis.crypto?.randomUUID?.() ?? Date.now() + "-" + Math.random().toString(36).slice(2);
}

export default function App() {
  const [active, setActive] = useState<Feature>("Chat");
  const [draft, setDraft] = useState("");
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [documents, setDocuments] = useState<LocalDocument[]>([]);
  const [selectedDocument, setSelectedDocument] = useState<LocalDocument | null>(null);
  const [storageReady, setStorageReady] = useState(false);
  const [canPersist, setCanPersist] = useState(false);
  const [memoryEnabled, setMemoryEnabled] = useState(false);
  const [storageNotice, setStorageNotice] = useState("Carregando dados locais...");
  const fileInput = useRef<HTMLInputElement>(null);

  useEffect(() => {
    let cancelled = false;
    Promise.all([loadChatMessages(), loadMemoryEnabled(), loadDocuments()])
      .then(([savedMessages, savedMemoryEnabled, savedDocuments]) => {
        if (cancelled) return;
        setMessages(savedMessages);
        setMemoryEnabled(savedMemoryEnabled);
        setDocuments(savedDocuments);
        setCanPersist(true);
        setStorageNotice("Histórico, preferências e documentos carregados deste navegador.");
      })
      .catch(() => {
        if (!cancelled) {
          setCanPersist(false);
          setStorageNotice("Falha ao carregar os dados locais. A sessão não poderá sobrescrever os dados existentes.");
        }
      })
      .finally(() => { if (!cancelled) setStorageReady(true); });
    return () => { cancelled = true; };
  }, []);

  useEffect(() => {
    if (!storageReady || !canPersist) return;
    let cancelled = false;
    saveChatMessages(messages).then(
      () => { if (!cancelled) setStorageNotice("Histórico salvo neste navegador."); },
      () => { if (!cancelled) setStorageNotice("Falha ao salvar o histórico local."); },
    );
    return () => { cancelled = true; };
  }, [messages, storageReady, canPersist]);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const cleanMessage = draft.trim();
    if (!cleanMessage || cleanMessage.length > CHAT_MESSAGE_LIMIT || !canPersist) return;
    setMessages((current) => [
      ...current,
      { id: createId(), text: cleanMessage, createdAt: Date.now() },
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

  async function handleDocumentImport(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];
    event.target.value = "";
    if (!file) return;
    const lowerName = file.name.toLowerCase();
    if (!lowerName.endsWith(".txt") && !lowerName.endsWith(".md")) {
      setStorageNotice("Formato não permitido. Selecione um arquivo .txt ou .md.");
      return;
    }
    if (file.size > DOCUMENT_BYTE_LIMIT) {
      setStorageNotice("O arquivo ultrapassa o limite de 1 MB.");
      return;
    }
    try {
      const content = await file.text();
      if (!content.trim()) {
        setStorageNotice("O documento está vazio.");
        return;
      }
      if (new TextEncoder().encode(content).byteLength > DOCUMENT_BYTE_LIMIT) {
        setStorageNotice("O conteúdo ultrapassa o limite de 1 MB.");
        return;
      }
      const document: LocalDocument = {
        id: createId(),
        name: file.name.slice(0, 255),
        content,
        createdAt: Date.now(),
      };
      await saveDocument(document);
      const updated = await loadDocuments();
      setDocuments(updated);
      setSelectedDocument(updated.find((item) => item.id === document.id) ?? null);
      setStorageNotice("Documento importado e salvo neste navegador.");
    } catch {
      setStorageNotice("Não foi possível importar ou salvar o documento.");
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
            >{item}</button>
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
                  <p>O histórico é persistido localmente. O motor de IA ainda será integrado; por enquanto, não há respostas automáticas.</p>
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
              <button type="submit" disabled={!draft.trim() || !canPersist}>Enviar</button>
            </form>
            <p className="notice" role="status">{storageNotice}</p>
            <p className="notice">Limite local: 300 mensagens. Os dados permanecem neste navegador; limpar os dados do site pode apagá-los.</p>
            <button className="secondary-action" type="button" onClick={handleClearHistory} disabled={!canPersist || messages.length === 0}>Apagar histórico</button>
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
            <p>Esta preferência fica salva localmente. A memória inteligente ainda não está conectada. Desativar esta opção não apaga o histórico do chat.</p>
            <p className="notice" role="status">{storageNotice}</p>
          </section>
        ) : active === "Documentos" ? (
          <section className="module-placeholder documents-module">
            <h2>Biblioteca de documentos</h2>
            <p>Importe arquivos .txt ou .md. Limite de 1 MB por arquivo e 20 documentos salvos localmente.</p>
            <input
              ref={fileInput}
              className="file-input"
              type="file"
              accept=".txt,.md,text/plain,text/markdown"
              onChange={(event) => void handleDocumentImport(event)}
              disabled={!canPersist}
            />
            <button className="secondary-action" type="button" onClick={() => fileInput.current?.click()} disabled={!canPersist}>Importar documento</button>
            <p className="notice" role="status">{storageNotice}</p>
            {documents.length === 0 ? <p>Nenhum documento importado.</p> : (
              <ul className="document-list">
                {documents.map((document) => (
                  <li key={document.id}>
                    <button
                      className="document-select"
                      type="button"
                      onClick={() => setSelectedDocument(selectedDocument?.id === document.id ? null : document)}
                    >{document.name}</button>
                  </li>
                ))}
              </ul>
            )}
            {selectedDocument && (
              <article className="document-preview">
                <h3>{selectedDocument.name}</h3>
                <pre>{selectedDocument.content}</pre>
              </article>
            )}
            <p className="notice">O conteúdo é exibido como texto simples. A IA ainda não analisa documentos; PDF e OCR não estão disponíveis.</p>
          </section>
        ) : (
          <div className="module-placeholder">
            <h2>{active}</h2>
            <p>Este módulo será desenvolvido separadamente. Ainda não há geração de mangá ou geração de imagens nesta versão.</p>
          </div>
        )}
      </section>
    </main>
  );
}
