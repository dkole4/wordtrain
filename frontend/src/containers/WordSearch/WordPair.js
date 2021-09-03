import React from 'react'
import PropTypes from 'prop-types'
import { Button, Table } from 'semantic-ui-react'


export const WordPair = ({ wordPair, added, addWord }) => {
  return (
    <Table.Row>
      <Table.Cell>
        { wordPair.word }
      </Table.Cell>
      <Table.Cell>
        { wordPair.translation }
      </Table.Cell>
      <Table.Cell>
        { wordPair.lang_word} / { wordPair.lang_translation }
      </Table.Cell>
      <Table.Cell collapsing textAlign='right'>
        <Button positive={!added} disabled={added} onClick={() => addWord(wordPair)}>
          { added ? 'Added' : 'Add' }
        </Button>
      </Table.Cell>
    </Table.Row>
  )
}

WordPair.propTypes = {
  wordPair: PropTypes.object.isRequired,
  added: PropTypes.bool.isRequired,
  addWord: PropTypes.func.isRequired
}