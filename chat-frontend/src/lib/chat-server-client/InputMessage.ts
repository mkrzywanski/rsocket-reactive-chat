
export class InputMessage {
    private usernameFrom: String;
    private content: String;
    private chatRoomId: String;

    constructor(usernameFrom: String,
        content: String,
        chatRoomId: String) {
        this.chatRoomId = chatRoomId;
        this.usernameFrom = usernameFrom;
        this.content = content;
    }
}