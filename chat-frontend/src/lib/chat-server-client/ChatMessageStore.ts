import { Message } from "../api/Message";

export class ChatMessageStore {

    private cache: Map<String, Array<Message>>

    constructor() {
        this.cache = new Map()
    }

    putMessageToChat(chatId: string, message: Message) {
        const messages = this.cache.get(chatId);

        if (messages == null) {
            const a: Array<Message> = []
            a.push(message)
            this.cache.set(chatId, a)
        } else {
            messages.push(message)
            messages.sort((a, b) => new Date(b.time).getTime() - new Date(a.time).getTime())
            this.cache.set(chatId, [...messages])
        }
    }

    putMessagesToChat(chatId: string, messages: Message[]) {
       messages.forEach(m => this.putMessageToChat(chatId, m))
    }

    get(chatId: string): Array<Message> {
        const messages = this.cache.get(chatId)
        if (messages === undefined) {
            return []
        } else {
            return [...messages];
        }
    }
}