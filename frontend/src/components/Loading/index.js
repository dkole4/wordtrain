import React from 'react'
import { Header, Icon, Segment } from 'semantic-ui-react'

export const Loading = () => (
  <Segment placeholder textAlign='center'>
    <Header as='h2' icon>
      <Icon loading name='circle notch' />
      Loading...
      <Header.Subheader>
        Setting up the application...
      </Header.Subheader>
    </Header>
  </Segment>
)