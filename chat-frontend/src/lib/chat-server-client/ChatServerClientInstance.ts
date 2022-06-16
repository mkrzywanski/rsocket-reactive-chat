import { ChatServerClient } from "./ChatServerClient";

// export const rsocketInstance : ChatServerClient = getUserNames();

async function getUserNames() {
    return await ChatServerClient.CreateAsync("localhost", 9090);
}