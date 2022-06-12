import { useKeycloak } from "@react-keycloak/web";
import React, { FC, useContext } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { JwtAuthUserMetadataProvider } from "../../lib/auth/JwtAuthUserMetadataProvider";
import { RsocketContext } from "../../lib/chat-server-client/RsocketContext";

interface JoinChatByLinkProps {
  addChat: (chat: string) => void;
}

const JoinChatByLink: FC<JoinChatByLinkProps> = (
  props: JoinChatByLinkProps
) => {
  const [searchParams] = useSearchParams();
  const { keycloak } = useKeycloak();
  const rsocket = useContext(RsocketContext);
  const navigate = useNavigate();
  const chatId = searchParams.get("chatId") ?? "";

  if (!keycloak.authenticated) {
    const redirectUrl =
      "http://" + window.location.host + "/joinChat?chatId=" + chatId;
    keycloak.login({ redirectUri: redirectUrl });
  } else {
    const jwtMetadata = new JwtAuthUserMetadataProvider(keycloak.token || "");
    rsocket?.joinChat(jwtMetadata, chatId, (ignored) => {
      navigate("/chat");
    });
  }

  return <div data-testid="JoinChatByLink"></div>;
};

export default JoinChatByLink;
