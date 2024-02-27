import { useAppSelector } from "@application/store"
import { useUserApi } from "@infrastructure/apis/api-management";
import { UserRoleEnum } from "@infrastructure/apis/client";
import { useQuery } from "@tanstack/react-query";
import { isNull, isUndefined } from "lodash";

/**
 * You can use this hook to retrieve the own user from the backend.
 * You can create new hooks by using and combining other hooks.
 */
export const useOwnUser = () => {
    const { userId } = useAppSelector(x => x.profileReducer); // Get the own user id from the redux storage.
    const { getUser: { key: queryKey, query } } = useUserApi(); // Get the client for the API.
    const { data } = useQuery({
        queryKey: [queryKey, userId],
        queryFn: () => {
            if (isNull(userId)) { // Use conditions within the hook callback, otherwise the framework will throw an error because the hooks are not called in order.
                return null;
            }

            return query(userId);
        },
        refetchInterval: Infinity, // User information may not be frequently updated so refetching the data periodically is not necessary.
        refetchOnWindowFocus: false // This disables fetching the user information from the backend when focusing on the current window.
    });

    return data?.response;
}

/**
 * This hook returns if the current user has the given role.
 */
export const useOwnUserHasRole = (role: UserRoleEnum) => {
    const ownUser = useOwnUser();

    if (isUndefined(ownUser)) {
        return;
    }

    return ownUser.role === role;
}

/**
 * This hook returns if the JWT token has expired or not.
 */
export const useTokenHasExpired = () => {
    const { loggedIn, exp } = useAppSelector(x => x.profileReducer);
    const now = Date.now() / 1000;

    return {
        loggedIn,
        hasExpired: !exp || exp < now
    };
}