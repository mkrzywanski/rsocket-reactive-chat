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

    toObject(){
        return {
            usernameFrom : this.usernameFrom, 
            content : this.content,
            chatRoomId : this.chatRoomId
        }
    }

    serialize() {
        return JSON.stringify(this.toObject());
    }

    static fromJSON(serialized : string) : InputMessage {
        const inputMessage : ReturnType<InputMessage["toObject"]> = JSON.parse(serialized);

        return new InputMessage(
            inputMessage.usernameFrom,
            inputMessage.content,
            inputMessage.chatRoomId
        )
    }
}