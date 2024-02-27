import { Configuration } from "@infrastructure/apis/client";
import { isNull } from "lodash";

/**
 * This function returns the configuration to the generated API client with the authentication header.
 */
export const getAuthenticationConfiguration = (token: string | null) => new Configuration(!isNull(token) ? { apiKey: `Bearer ${token}` } : {});