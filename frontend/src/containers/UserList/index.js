import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Container, Table } from 'semantic-ui-react'

import { setErrorMessage } from 'store/actions/notification-actions'
import { logout } from 'store/actions/auth-actions'
import { userService } from 'services/user-service'

export const UserList = () => {
  const [users, setUsers] = useState([])
  
  const dispatch = useDispatch()
  const user = useSelector(state => state.loggedUser)
  
  const getDate = (date) =>
    date.split('.')[0].replaceAll('-', '/').replace('T', ' ')
  
  const getLevel = (tries) =>
    Math.floor(Math.sqrt(tries / 10))

  useEffect( async () => {
    const response = await userService.getUsers(user)
    
    if (response.status === 200) {
      const data = response.data
      if (data) {
        setUsers(data)
      } else {
        dispatch(
          setErrorMessage(
            'An error occured during user information fetching. ' +
            'Please reload the page or report the error to developers.'
          )
        )    
      }
    } else {
      dispatch(logout())
    }
  }, [])

  return (
    <Container>
      <Table>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell>Username</Table.HeaderCell>
            <Table.HeaderCell>Tries</Table.HeaderCell>
            <Table.HeaderCell>Word count</Table.HeaderCell>
            <Table.HeaderCell>Last seen</Table.HeaderCell>
            <Table.HeaderCell>Joined</Table.HeaderCell>
          </Table.Row>
        </Table.Header>

        <Table.Body>
          { users.map(user =>
            <Table.Row key={user.id}>
              <Table.Cell>{ user.username }</Table.Cell>
              <Table.Cell>{ getLevel(user.tries) } lvl ({ user.tries }) </Table.Cell>
              <Table.Cell>{ user.word_count }</Table.Cell>
              <Table.Cell>{ getDate(user.last_seen) }</Table.Cell>
              <Table.Cell>{ getDate(user.joined) }</Table.Cell>
            </Table.Row>
          )}
        </Table.Body>
      </Table>
    </Container>
  )
}