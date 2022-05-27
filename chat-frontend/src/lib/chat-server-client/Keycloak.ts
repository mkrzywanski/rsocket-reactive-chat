import Keycloak from "keycloak-js";

const keycloak = new Keycloak({
    url: "http://localhost:8081/auth",
    realm: "chat",
    clientId: "chat-frontend",
});

export default keycloak;