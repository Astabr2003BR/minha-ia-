export type ChatMessage = {
  id: string;
  text: string;
  createdAt: number;
};

type StoredConversation = {
  schemaVersion: 1;
  messages: ChatMessage[];
};

const DATABASE_NAME = "minha-ia-local";
const STORE_NAME = "conversations";
const CONVERSATION_KEY = "main";
const MAX_MESSAGES = 300;
const MAX_MESSAGE_LENGTH = 10_000;

function isChatMessage(value: unknown): value is ChatMessage {
  if (typeof value !== "object" || value === null) return false;
  const candidate = value as Record<string, unknown>;
  return (
    typeof candidate.id === "string" &&
    candidate.id.length > 0 &&
    typeof candidate.text === "string" &&
    candidate.text.length <= MAX_MESSAGE_LENGTH &&
    typeof candidate.createdAt === "number" &&
    Number.isFinite(candidate.createdAt)
  );
}

function openDatabase(): Promise<IDBDatabase> {
  return new Promise((resolve, reject) => {
    if (typeof indexedDB === "undefined") {
      reject(new Error("Armazenamento local não disponível neste navegador."));
      return;
    }

    const request = indexedDB.open(DATABASE_NAME, 1);
    request.onupgradeneeded = () => {
      const database = request.result;
      if (!database.objectStoreNames.contains(STORE_NAME)) {
        database.createObjectStore(STORE_NAME);
      }
    };
    request.onsuccess = () => {
      const database = request.result;
      database.onversionchange = () => database.close();
      resolve(database);
    };
    request.onerror = () => reject(request.error ?? new Error("Falha ao abrir o banco local."));
    request.onblocked = () => reject(new Error("Atualização do banco local bloqueada por outra aba."));
  });
}

export async function loadChatMessages(): Promise<ChatMessage[]> {
  const database = await openDatabase();
  try {
    return await new Promise((resolve, reject) => {
      const transaction = database.transaction(STORE_NAME, "readonly");
      const request = transaction.objectStore(STORE_NAME).get(CONVERSATION_KEY);
      request.onsuccess = () => {
        const stored: unknown = request.result;
        if (stored === undefined) {
          resolve([]);
          return;
        }
        if (
          typeof stored !== "object" ||
          stored === null ||
          (stored as StoredConversation).schemaVersion !== 1 ||
          !Array.isArray((stored as StoredConversation).messages)
        ) {
          reject(new Error("O histórico local está em formato inválido."));
          return;
        }
        const messages = (stored as StoredConversation).messages;
        if (!messages.every(isChatMessage)) {
          reject(new Error("O histórico local contém dados inválidos."));
          return;
        }
        resolve(messages.slice(-MAX_MESSAGES));
      };
      request.onerror = () => reject(request.error ?? new Error("Falha ao ler o histórico local."));
      transaction.onabort = () => reject(transaction.error ?? new Error("Leitura do histórico cancelada."));
    });
  } finally {
    database.close();
  }
}

export async function saveChatMessages(messages: ChatMessage[]): Promise<void> {
  if (!Array.isArray(messages) || !messages.every(isChatMessage)) {
    throw new Error("Não foi possível salvar: mensagem inválida.");
  }

  const boundedMessages = messages.slice(-MAX_MESSAGES);
  const database = await openDatabase();
  try {
    await new Promise<void>((resolve, reject) => {
      const transaction = database.transaction(STORE_NAME, "readwrite");
      transaction.objectStore(STORE_NAME).put(
        { schemaVersion: 1, messages: boundedMessages } satisfies StoredConversation,
        CONVERSATION_KEY,
      );
      transaction.oncomplete = () => resolve();
      transaction.onerror = () => reject(transaction.error ?? new Error("Falha ao salvar o histórico local."));
      transaction.onabort = () => reject(transaction.error ?? new Error("Salvamento do histórico cancelado."));
    });
  } finally {
    database.close();
  }
}

export const CHAT_MESSAGE_LIMIT = MAX_MESSAGE_LENGTH;
export const CHAT_HISTORY_LIMIT = MAX_MESSAGES;
