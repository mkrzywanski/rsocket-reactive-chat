import { useKeycloak } from "@react-keycloak/web";

const PrivateRoute = ({ protectedComponent: children }: { protectedComponent: JSX.Element }) => {
    const { keycloak } = useKeycloak();

    const isLoggedIn = keycloak.authenticated;

    console.log(keycloak.token)

    return isLoggedIn ? children : null;
};

export default PrivateRoute;