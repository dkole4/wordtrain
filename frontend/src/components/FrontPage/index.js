import React from 'react'
import { Container, Header, Segment } from 'semantic-ui-react'
import { UserList } from '../../containers/UserList'


export const FrontPage = () => (
  <Container>
    <Segment>
      <Header>Welcome to WordTrain version 0.1</Header>
    </Segment>
    <UserList />
  </Container>
)