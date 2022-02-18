import React from 'react'
import { Container, Header, Segment } from 'semantic-ui-react'

import { UserList } from '../UserList'


export const FrontPage = () => {
  return (
    <Container>
      <Segment>
        <Header>Welcome to WordTrain version 0.2</Header>
      </Segment>
      <UserList />
    </Container>
  )
}