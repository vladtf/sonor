import { jwtDecode } from "jwt-decode";

function getClaimFromToken(token, claimKey) {
  try {
    const decodedToken = jwtDecode(token);
    return decodedToken[claimKey]; // Access the specific claim from the decoded token
  } catch (error) {
    console.error("Failed to decode JWT:", error);
    return null;
  }
}

export { getClaimFromToken };