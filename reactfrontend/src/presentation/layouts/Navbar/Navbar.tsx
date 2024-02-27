import { useCallback } from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Button from '@mui/material/Button';
import HomeIcon from '@mui/icons-material/Home';
import { Link } from 'react-router-dom';
import { AppRoute } from 'routes';
import { useIntl } from 'react-intl';
import { useAppDispatch, useAppSelector } from '@application/store';
import { Grid } from '@mui/material';
import { resetProfile } from '@application/state-slices';
import { useAppRouter } from '@infrastructure/hooks/useAppRouter';
import { NavbarLanguageSelector } from '@presentation/components/ui/NavbarLanguageSelector/NavbarLanguageSelector';
import { useOwnUserHasRole } from '@infrastructure/hooks/useOwnUser';
import { UserRoleEnum } from '@infrastructure/apis/client';

/**
 * This is the navigation menu that will stay at the top of the page.
 */
export const Navbar = () => {
  const { formatMessage } = useIntl();
  const { loggedIn } = useAppSelector(x => x.profileReducer);
  const isAdmin = useOwnUserHasRole(UserRoleEnum.Admin);
  const dispatch = useAppDispatch();
  const { redirectToHome } = useAppRouter();
  const logout = useCallback(() => {
    dispatch(resetProfile());
    redirectToHome();
  }, [dispatch, redirectToHome]);

  return <Box sx={{ flexGrow: 1 }}>
    <AppBar>
      <Toolbar>
        <Grid
          container
          item
          direction="row"
          xs={12}
          alignItems="center"
          wrap="nowrap"
          columnSpacing={2}
        >
          <Grid container item direction="column" xs={1}>
            <Link
              to={AppRoute.Index}> {/* Add a button to redirect to the home page. */}
              <HomeIcon style={{ color: 'white' }} fontSize='large' />
            </Link>
          </Grid>
          <Grid container item direction="column" xs={8}>
            {isAdmin && <Grid // If the user is logged in and it is an admin they can have new menu items shown.
              container
              item
              direction="row"
              xs={12}
              alignItems="center"
              wrap="nowrap"
              columnSpacing={15}
            >
              <Grid container item direction="column" xs={1}>
                <Button color="inherit">
                  <Link style={{ color: 'white' }} to={AppRoute.Users}>
                    {formatMessage({ id: "globals.users" })}
                  </Link>
                </Button>
              </Grid>
              <Grid container item direction="column" xs={1}>
                <Button color="inherit">
                  <Link style={{ color: 'white' }} to={AppRoute.UserFiles}>
                    {formatMessage({ id: "globals.files" })}
                  </Link>
                </Button>
              </Grid>
            </Grid>}
          </Grid>
          <Grid container item direction="column" xs={1}>
            <NavbarLanguageSelector />
          </Grid>
          <Grid container item direction="column" xs={2}>
            {!loggedIn && <Button color="inherit">  {/* If the user is not logged in show a button that redirects to the login page. */}
              <Link style={{ color: 'white' }} to={AppRoute.Login}>
                {formatMessage({ id: "globals.login" })}
              </Link>
            </Button>}
            {loggedIn && <Button onClick={logout} color="inherit" > {/* Otherwise show the logout button. */}
              {formatMessage({ id: "globals.logout" })}
            </Button>}
          </Grid>
        </Grid>
      </Toolbar>
    </AppBar>
  </Box>
}