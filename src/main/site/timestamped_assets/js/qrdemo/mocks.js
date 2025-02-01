// Mocks:

export var forms = [
    {
        name: 'Form 1123 - Generated',
        qr: {q: '001002A'}
    }
]

export var formMock = {
    form: {
        name: 'Form 1123 - Generated',
        description: 'Form 1123 - Generated Description',
        blocks: [
            {
                name: 'Block 1 - Generated',
                description: 'Block 1 - Generated Description',
                fields: [
                    {
                        name: 'Field1',
                        placeholder: 'Place here name of engine...',
                        type: 'TEXT',
                        isStatic: false,
                        isPublic: true
                    },
                    {
                        name: 'Field2',
                        placeholder: 'Place here engine type...',
                        type: 'TEXT',
                        isStatic: true,
                        isPublic: true
                    },
                    {
                        name: 'Field3',
                        placeholder: 'Place here engine number...',
                        type: 'NUMBER',
                        isStatic: false,
                        isPublic: true
                    }
                ]
            },
            {
                name: 'Block 2 - Generated',
                description: 'Block 2 - Generated Description',
                fields: [
                    {
                        name: 'SpecificField1',
                        placeholder: 'Squish type...',
                        type: 'TEXT',
                        isStatic: false,
                        isPublic: true
                    },
                    {
                        name: 'SpecificField2',
                        placeholder: 'Number of rounds...',
                        type: 'NUMBER',
                        isStatic: false,
                        isPublic: true
                    },
                    {
                        name: 'SpecificField3',
                        placeholder: 'Files...',
                        type: 'FILE',
                        isStatic: false,
                        isPublic: true
                    }
                ]
            }
        ],
        qr: {q: '001002A'}
    },
    data: {
        Field1: 'Engine super fast 123',
        Field2: 'Machine engine',
        Field3: 1008,
        SpecificField1: 'Fast',
        SpecificField2: 2800,
        SpecificField3: 'File123.txt'
    }
}
