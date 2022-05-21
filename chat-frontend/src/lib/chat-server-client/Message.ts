export class Message {
    private usernameFrom: String;
    private content: String;
    private chatRoomId: String;
    private timestamp : Date;

    constructor(usernameFrom: String,
        content: String,
        chatRoomId: String,
        timestamp : Date) {
        this.chatRoomId = chatRoomId;
        this.usernameFrom = usernameFrom;
        this.content = content;
        this.timestamp = timestamp;
    }

    toObject(){
        return {
            usernameFrom : this.usernameFrom, 
            content : this.content,
            chatRoomId : this.chatRoomId,
            timestamp : this.timestamp
        }
    }

    serialize() {
        return JSON.stringify(this.toObject());
    }

    static fromJSON(serialized : string) : Message {
        const message : ReturnType<Message["toObject"]> = JSON.parse(serialized);

        return new Message(
            message.usernameFrom,
            message.content,
            message.chatRoomId,
            message.timestamp
        )
    }
}