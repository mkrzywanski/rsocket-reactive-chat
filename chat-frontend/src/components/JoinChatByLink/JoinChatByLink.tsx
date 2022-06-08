import { useKeycloak } from "@react-keycloak/web";
import React, { FC, useContext } from "react";
import { useSearchParams } from "react-router-dom";
import { JwtAuthUserMetadataProvider } from "../../lib/auth/JwtAuthUserMetadataProvider";
import { ChatServerClient } from "../../lib/chat-server-client/ChatServerClient";
import { RsocketContext } from "../../lib/chat-server-client/RsocketContext";
import styles from "./JoinChatByLink.module.css";

interface JoinChatByLinkProps {

}

const JoinChatByLink: FC<JoinChatByLinkProps> = (props : JoinChatByLinkProps) => {
  const [searchParams, setSearchParams] = useSearchParams();
  const { keycloak } = useKeycloak();
  const rsocket = useContext(RsocketContext)

  const chatId = searchParams.get("chatId");

  const redirectUrl = window.location.host + "/joinChat?chatId=" + chatId

  const jwtMetadata = new JwtAuthUserMetadataProvider(keycloak.token || "");

  if (!keycloak.authenticated) {
    keycloak.login({redirectUri: redirectUrl})
  } else {
    // rsocket?.joinChat(jwtMetadata, chatId, (a) => {})

  }

  return null;
};

export default JoinChatByLink;
