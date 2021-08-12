import React from 'react'
import { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Container, Header, Segment, Table } from 'semantic-ui-react'
import { setErrorMessage } from '../reducer-notification'
import { logout } from '../reducer-user'
import { userService } from '../services'


const getScore = (user) => {
  if (!user.score || !user.tries)
    return 0

  return Math.round(user.score / user.tries * 100)
}

const getDate = (date) =>
  date.split('.')[0].replaceAll('-', '/')

export const FrontPage = () => {

  return (
    <Container>
      <Segment>
        <Header>Welcome to WordApp version 0.1</Header>
      </Segment>
      <UserList />
    </Container>
  )
}

export const UserList = () => {
  const [users, setUsers] = useState([])
  
  const dispatch = useDispatch()
  const user = useSelector(state => state.loggedUser)

  useEffect( async () => {
    const response = await userService.getUsers(user)
    
    if (response.status === 200) {
      const data = response.data
      data.sort((a, b) => getScore(b) - getScore(a))
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
            <Table.HeaderCell>Score</Table.HeaderCell>
            <Table.HeaderCell>Word count</Table.HeaderCell>
            <Table.HeaderCell>Last seen</Table.HeaderCell>
          </Table.Row>
        </Table.Header>

        <Table.Body>
          { users.map(user =>
            <Table.Row key={user.id}>
              <Table.Cell>{ user.username }</Table.Cell>
              <Table.Cell>{ getScore(user) } %</Table.Cell>
              <Table.Cell>{ user.word_count }</Table.Cell>
              <Table.Cell>{ getDate(user.last_seen) }</Table.Cell>
            </Table.Row>
          )}
        </Table.Body>
      </Table>
    </Container>
  )
}