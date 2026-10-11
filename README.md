## Vertical slabs for everyone

Lets you rotate a slab in your hand (by right-clicking while not targeting any block) 
to place it vertically.

Horizontal and vertical slabs can stack, no matter how they are rotated.

---

## Download

### Curseforge

### Modrinth

---


## Modded slabs

Modded slabs require a resource pack to be displayed properly. 

Each slab requires 2 json files: `blockstate` and `item model`:

### Blockstate template

For `assets/<modid>/blockstates/`:

```json
{
  "multipart": [
    {
      "apply": {
        "model": "MODID:block/FULL_BLOCK_ID"
      },
      "when": {
        "type": "double"
      }
    },
    {
      "apply": {
        "model": "MODID:block/SLAB_ID"
      },
      "when": {
        "type": "bottom",
        "alignment": "horizontal"
      }
    },
    {
      "apply": {
        "model": "MODID:block/SLAB_ID",
        "uvlock": true,
        "x": 90
      },
      "when": {
        "type": "bottom",
        "alignment": "vertical_north"
      }
    },
    {
      "apply": {
        "model": "MODID:block/SLAB_ID",
        "uvlock": true,
        "z": 90
      },
      "when": {
        "type": "bottom",
        "alignment": "vertical_west"
      }
    },
    {
      "apply": {
        "model": "MODID:block/SLAB_TOP_ID"
      },
      "when": {
        "type": "top",
        "alignment": "horizontal"
      }
    },
    {
      "apply": {
        "model": "MODID:block/SLAB_TOP_ID",
        "uvlock": true,
        "x": 90
      },
      "when": {
        "type": "top",
        "alignment": "vertical_north"
      }
    },
    {
      "apply": {
        "model": "MODID:block/SLAB_TOP_ID",
        "uvlock": true,
        "z": 90
      },
      "when": {
        "type": "top",
        "alignment": "vertical_west"
      }
    }
  ]
}
```

### Item models

For `assets/<modid>/items/`:

```json
{
  "model": {
    "type": "minecraft:select",
    "cases": [
      {
        "model": {
          "type": "minecraft:model",
          "model": "MODID:block/SLAB_ID",
          "transformation": {
            "left_rotation": [
              0.0,
              0.0,
              0.0,
              1.0
            ],
            "right_rotation": [
              0.0,
              0.0,
              0.707,
              0.707
            ],
            "scale": [
              1.0,
              1.0,
              1.0
            ],
            "translation": [
              0.707,
              0.0,
              0.0
            ]
          }
        },
        "when": true
      },
      {
        "model": {
          "type": "minecraft:model",
          "model": "MODID:block/SLAB_ID"
        },
        "when": false
      }
    ],
    "component": "frycvertslabs:slab_vertical",
    "fallback": {
      "type": "minecraft:model",
      "model": "MODID:block/SLAB_ID"
    },
    "property": "minecraft:component"
  }
}
```


