import { useIntl } from "react-intl";
import { isUndefined } from "lodash";
import { IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TablePagination, TableRow } from "@mui/material";
import { DataLoadingContainer } from "../../LoadingDisplay";
import { useUserFileTableController } from "./UserFileTable.controller";
import { UserFileDTO } from "@infrastructure/apis/client";
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';
import FileOpenIcon from '@mui/icons-material/FileOpen';
import { dateToDateStringOrNull } from "@infrastructure/utils/dateUtils";
import { UserFileAddDialog } from "../../Dialogs/UserFileAddDialog";

/**
 * This hook returns a header for the table with translated columns.
 */
const useHeader = (): { key: keyof UserFileDTO, name: string }[] => {
    const { formatMessage } = useIntl();

    return [
        { key: "name", name: formatMessage({ id: "globals.name" }) },
        { key: "description", name: formatMessage({ id: "globals.description" }) },
        { key: "user", name: formatMessage({ id: "globals.addBy" }) },
        { key: "createdAt", name: formatMessage({ id: "globals.createdAt" }) }
    ]
};

/**
 * The values in the table are organized as rows so this function takes the entries and creates the row values ordering them according to the order map.
 */
const getRowValues = (entries: UserFileDTO[] | null | undefined, orderMap: { [key: string]: number }) =>
    entries?.map(
        entry => {
            return {
                entry: entry,
                data: Object.entries(entry).filter(([e]) => !isUndefined(orderMap[e])).sort(([a], [b]) => orderMap[a] - orderMap[b]).map(([key, value]) => { return { key, value } })
            }
        });

/**
 * For some values there may need to have special renders, you can use a map of render functions.
 */
const renders: { [key: string]: (value: any) => string | null } = {
    createdAt: dateToDateStringOrNull,
    user: (value) => value.name
};

/**
 * Creates the user file table.
 */
export const UserFileTable = () => {
    const { formatMessage } = useIntl();
    const header = useHeader();
    const orderMap = header.reduce((acc, e, i) => { return { ...acc, [e.key]: i } }, {}) as { [key: string]: number }; // Get the header column order.
    const { handleChangePage, handleChangePageSize, pagedData, isError, isLoading, tryReload, labelDisplay, downloadUserFile, openUserFile } = useUserFileTableController(); // Use the controller hook.
    const rowValues = getRowValues(pagedData?.data, orderMap); // Get the row values.

    return <DataLoadingContainer isError={isError} isLoading={isLoading} tryReload={tryReload}> {/* Wrap the table into the loading container because data will be fetched from the backend and is not immediately available.*/}
        <UserFileAddDialog />  {/* Add the button to open the user file add modal. */}
        {!isUndefined(pagedData) && !isUndefined(pagedData?.totalCount) && !isUndefined(pagedData?.page) && !isUndefined(pagedData?.pageSize) &&
            <TablePagination // Use the table pagination to add the navigation between the table pages.
                component="div"
                count={pagedData.totalCount} // Set the entry count returned from the backend.
                page={pagedData.totalCount !== 0 ? pagedData.page - 1 : 0} // Set the current page you are on.
                onPageChange={handleChangePage} // Set the callback to change the current page.
                rowsPerPage={pagedData.pageSize} // Set the current page size.
                onRowsPerPageChange={handleChangePageSize}
                labelRowsPerPage={formatMessage({ id: "labels.itemsPerPage" })}
                labelDisplayedRows={labelDisplay} // Set the callback to change the current page size. 
                showFirstButton
                showLastButton
            />}

        <TableContainer component={Paper}>
            <Table>
                <TableHead>
                    <TableRow>
                        {
                            header.map(e => <TableCell key={`header_${String(e.key)}`}>{e.name}</TableCell>) // Add the table header.
                        }
                        <TableCell>{formatMessage({ id: "labels.actions" })}</TableCell> {/* Add additional header columns if needed. */}
                    </TableRow>
                </TableHead>
                <TableBody>
                    {
                        rowValues?.map(({ data, entry }, rowIndex) => <TableRow key={`row_${rowIndex + 1}`}>
                            {data.map((keyValue, index) => {
                                return <TableCell key={`cell_${rowIndex + 1}_${index + 1}`}>{isUndefined(renders[keyValue.key]) ? keyValue.value : renders[keyValue.key](keyValue.value)}</TableCell> // Add the row values.
                            })}
                            <TableCell> {/* Add other cells like action buttons. */}
                                {<IconButton color="primary" onClick={() => downloadUserFile(entry)}>
                                    <CloudDownloadIcon color="primary" fontSize='small' />
                                </IconButton>}
                                {entry.name?.endsWith(".pdf") && <IconButton color="primary" onClick={() => openUserFile(entry)}>
                                    <FileOpenIcon color="primary" fontSize='small' />
                                </IconButton>}
                            </TableCell>
                        </TableRow>)
                    }
                </TableBody>
            </Table>
        </TableContainer>
    </DataLoadingContainer >
}