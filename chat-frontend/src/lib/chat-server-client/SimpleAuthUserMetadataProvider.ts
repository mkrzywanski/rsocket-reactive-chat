import { encodeSimpleAuthMetadata } from "rsocket-core";
import { AuthMetadataProvider } from "./AuthMetadataProvider";

export class SimpleAuthUserMetadataProvider implements AuthMetadataProvider {

    private username : string;
    private password : string;

    constructor(username : string, password : string) {
        this.username = username
        this.password = password
    }

    userMetadata(): Buffer {
        return encodeSimpleAuthMetadata(this.username, this.password)
    }

}