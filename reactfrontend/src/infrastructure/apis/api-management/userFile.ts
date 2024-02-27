import { useAppSelector } from "@application/store";
import { ApiUserFileAddPostRequest, ApiUserFileGetPageGetRequest, UserFileApi } from "../client";
import { getAuthenticationConfiguration } from "@infrastructure/utils/userUtils";

/**
 * Use constants to identify mutations and queries.
 */
const getUserFilesQueryKey = "getUserFilesQuery";
const downloadUserFileQueryKey = "downloadUserFileQuery";
const addUserFileMutationKey = "addUserFileMutation";

/**
 * Returns the an object with the callbacks that can be used for the React Query API, in this case to manage the user API.
 */
export const useUserFileApi = () => {
    const { token } = useAppSelector(x => x.profileReducer); // You can use the data form the Redux storage.
    const config = getAuthenticationConfiguration(token); // Use the token to configure the authentication header.

    const getUserFiles = (page: ApiUserFileGetPageGetRequest) => new UserFileApi(config).apiUserFileGetPageGet(page); // Use the generated client code and adapt it.
    const downloadUserFile = (id: string) => new UserFileApi(config).apiUserFileDownloadIdGet({ id });
    const addUserFile = (userFile: ApiUserFileAddPostRequest) => new UserFileApi(config).apiUserFileAddPost(userFile);

    return {
        getUserFiles: { // Return the query object.
            key: getUserFilesQueryKey, // Add the key to identify the query.
            query: getUserFiles // Add the query callback.
        },
        downloadUserFile: {
            key: downloadUserFileQueryKey, 
            query: downloadUserFile // You can use these callback even outside the React Query API.
        },
        addUserFile: { // Return the mutation object.
            key: addUserFileMutationKey, // Add the key to identify the mutation.
            mutation: addUserFile // Add the mutation callback.
        }
    }
}