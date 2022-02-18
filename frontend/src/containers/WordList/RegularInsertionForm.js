import React from 'react'
import PropTypes from 'prop-types'

import { Table, Button } from 'semantic-ui-react'

import { useField } from 'utils/useField'

export const RegularInsertionForm = ({ addWord }) => {
  const [word, resetWord] = useField('text')
  const [translation, resetTranslation] = useField('text')

  const addCurrentWord = () => { 
    addWord(word.value, translation.value)
    resetWord()
    resetTranslation()
  }

  return (
    <>
      <Table.Row>
        <Table.Cell textAlign='center'>
          <Button circular icon='plus' onClick={addCurrentWord} type='button'/>
        </Table.Cell>
        <Table.Cell>
          <input placeholder='Enter word' { ...word } />
        </Table.Cell>
        <Table.Cell />
        <Table.Cell>
          <input placeholder='Enter translation' { ...translation } />
        </Table.Cell>
      </Table.Row>
    </>
  )
}

RegularInsertionForm.propTypes = {
  addWord: PropTypes.func.isRequired
}