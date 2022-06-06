import { ISubscriber, ISubscription, Payload } from "rsocket-types";
import { Message } from "./Message";

export class MessageStreamSubscriber implements ISubscriber<Payload<any, any>> {

    private callback : (message : Message) => void
    private subscription? : ISubscription
 
     constructor(c : (message : Message) => void) {
         this.callback = c;
     }

    onComplete() {

    }

    onError(error: Error) {
        console.log(error)
    }

    onNext(value: Payload<any, any>) {
        const m : Message = JSON.parse(value.data)
        this.callback(m)
        this.subscription?.request(1)
    }

    onSubscribe(subscription: ISubscription) {
        this.subscription = subscription;
        this.subscription.request(1)
    }

}