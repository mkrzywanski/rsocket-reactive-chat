import { AuthMetadataProvider } from "./AuthMetadataProvider";
import { encodeBearerAuthMetadata } from "rsocket-core";

export class JwtAuthUserMetadataProvider implements AuthMetadataProvider {

    private jwt : string

    constructor(jwt : string) {
       this.jwt = jwt
    }

    userMetadata(): Buffer {
        return encodeBearerAuthMetadata(this.jwt)
    }

}